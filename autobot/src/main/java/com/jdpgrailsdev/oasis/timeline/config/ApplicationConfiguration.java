/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jdpgrailsdev.oasis.timeline.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.ITemplateEngine;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/** Main Spring application configuration. */
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import({
  ControllerConfiguration.class,
  MicrometerConfiguration.class,
  ThymeleafConfiguration.class,
  WebMvcConfiguration.class,
  WebSecurityConfiguration.class
})
public class ApplicationConfiguration {

  /**
   * Defines the {@link DateUtils} bean.
   *
   * @return The {@link DateUtils} bean.
   */
  @Bean
  public DateUtils dateUtils() {
    return new DateUtils();
  }

  /**
   * Overrides the {@link EnvironmentEndpoint} bean to ensure that various configuration properties
   * are obfuscated.
   *
   * @param environment The runtime environment.
   * @return The {@link EnvironmentEndpoint} with sanitized properties.
   */
  @Bean
  public EnvironmentEndpoint environmentEndpoint(final Environment environment) {
    /*
     * Custom override of the EnvironmentEndpoint Spring Boot actuator
     * to mask the "ENCRYPTION_KEYS" environment variable in addition
     * to the normal set of masked keys.
     */
    final EnvironmentEndpoint endpoint = new EnvironmentEndpoint(environment);
    endpoint.setKeysToSanitize(
        "INSERT_API_KEY",
        "NEW_RELIC_LICENSE_KEY",
        "SPRING_ACTUATOR_USERNAME",
        "SPRING_ACTUATOR_PASSWORD",
        "spring.security.user.name",
        "spring.security.user.password",
        "TWITTER_OAUTH_CONSUMER_KEY",
        "TWITTER_OAUTH_CONSUMER_SECRET",
        "TWITTER_OAUTH_ACCESS_TOKEN",
        "TWITTER_OAUTH_ACCESS_TOKEN_SECRET");
    return endpoint;
  }

  /**
   * Jackson {@link ObjectMapper} bean.
   *
   * @return A Jackson {@link ObjectMapper} bean.
   */
  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    return mapper;
  }

  /**
   * Defines the {@link Twitter} API client bean.
   *
   * @param oauthConsumerKey The OAuth consumer key value.
   * @param oauthConsumerSecret The OAuth consumer secret value.
   * @param oauthAccessToken The OAuth access token value.
   * @param oauthAccessTokenSecret The OAuth access token secret value.
   * @return The {@link Twitter} API client bean.
   */
  @Bean
  public Twitter twitterApi(
      @Value("${TWITTER_BASE_REST_URL:https://api.twitter.com/1.1/}") final String baseRestUrl,
      @Value("${TWITTER_OAUTH_CONSUMER_KEY}") final String oauthConsumerKey,
      @Value("${TWITTER_OAUTH_CONSUMER_SECRET}") final String oauthConsumerSecret,
      @Value("${TWITTER_OAUTH_ACCESS_TOKEN}") final String oauthAccessToken,
      @Value("${TWITTER_OAUTH_ACCESS_TOKEN_SECRET}") final String oauthAccessTokenSecret) {
    final twitter4j.conf.Configuration configuration =
        new ConfigurationBuilder()
            .setRestBaseURL(baseRestUrl)
            .setOAuthConsumerKey(oauthConsumerKey)
            .setOAuthConsumerSecret(oauthConsumerSecret)
            .setOAuthAccessToken(oauthAccessToken)
            .setOAuthAccessTokenSecret(oauthAccessTokenSecret)
            .build();
    return new TwitterFactory(configuration).getInstance();
  }

  /**
   * Defines the {@link TweetFormatUtils} bean.
   *
   * @param templateEngine The template engine used to render the tweet text.
   * @param tweetContext The {@link TweetContext}.
   * @return The {@link TweetFormatUtils} bean.
   */
  @Bean
  public TweetFormatUtils tweetFormatUtils(
      @Qualifier("textTemplateEngine") final ITemplateEngine templateEngine,
      final TweetContext tweetContext) {
    return new TweetFormatUtils(templateEngine, tweetContext);
  }

  /**
   * Defines the {@link TwitterTimelineEventScheduler} bean.
   *
   * @param dateUtils {@link DateUtils} bean.
   * @param meterRegistry Micrometer {@link MeterRegistry} bean.
   * @param timelineDataLoader The {@link TimelineDataLoader} bean.
   * @param tweetFormatUtils The {@link TweetFormatUtils} bean.
   * @param twitterApi The {@link Twitter} API client bean.
   * @return The {@link TwitterTimelineEventScheduler} bean.
   */
  @Bean
  public TwitterTimelineEventScheduler twitterTimelineEventScheduler(
      final DateUtils dateUtils,
      final MeterRegistry meterRegistry,
      final TimelineDataLoader timelineDataLoader,
      final TweetFormatUtils tweetFormatUtils,
      final Twitter twitterApi) {
    return new TwitterTimelineEventScheduler.Builder()
        .withDateUtils(dateUtils)
        .withMeterRegistry(meterRegistry)
        .withTimelineDataLoader(timelineDataLoader)
        .withTweetFormatUtils(tweetFormatUtils)
        .withTwitter(twitterApi)
        .build();
  }

  /**
   * Defines the {@link ResourcePatternResolver} bean used to find and load the data file.
   *
   * @return The {@link ResourcePatternResolver} bean.
   */
  @Bean
  public ResourcePatternResolver timelineDataFileResourceResolver() {
    return new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
  }

  /**
   * Defines the {@link TimelineDataLoader} bean.
   *
   * @param objectMapper A Jackson {@link ObjectMapper} instance.
   * @param timelineDataFileResourceResolver The data file resource resolver bean.
   * @return The {@link TimelineDataLoader} bean.
   */
  @Bean
  public TimelineDataLoader timelineDataLoader(
      final ObjectMapper objectMapper,
      final ResourcePatternResolver timelineDataFileResourceResolver) {
    return new TimelineDataLoader(objectMapper, timelineDataFileResourceResolver);
  }

  /**
   * Defines the {@link TweetContext} bean.
   *
   * @return The {@link TweetContext} bean.
   */
  @Bean
  @ConfigurationProperties(prefix = "tweet.context")
  public TweetContext tweetContext() {
    return new TweetContext();
  }
}
