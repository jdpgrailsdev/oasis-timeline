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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;

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

import io.micrometer.core.instrument.MeterRegistry;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import(value = {ControllerConfiguration.class, MicrometerConfiguration.class, ThymeleafConfiguration.class, WebSecurityConfiguration.class})
public class ApplicationConfiguration {

    @Bean
    public DateUtils dateUtils() {
        return new DateUtils();
    }

    @Bean
    public EnvironmentEndpoint environmentEndpoint(final Environment environment) {
        /*
         * Custom override of the EnvironmentEndpoint Spring Boot actuator
         * to mask the "ENCRYPTION_KEYS" environment variable in addition
         * to the normal set of masked keys.
         */
        final EnvironmentEndpoint endpoint = new EnvironmentEndpoint(environment);
        endpoint.setKeysToSanitize("INSERT_API_KEY", "NEW_RELIC_LICENSE_KEY",
                "SPRING_ACTUATOR_USERNAME", "SPRING_ACTUATOR_PASSWORD", "spring.security.user.name", "spring.security.user.password",
                "TWITTER_OAUTH_CONSUMER_KEY", "TWITTER_OAUTH_CONSUMER_SECRET", "TWITTER_OAUTH_ACCESS_TOKEN", "TWITTER_OAUTH_ACCESS_TOKEN_SECRET");
        return endpoint;
    }

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper;
    }

    @Bean
    public Twitter twitterApi(@Value("${TWITTER_OAUTH_CONSUMER_KEY}") final String oAuthConsumerKey,
            @Value("${TWITTER_OAUTH_CONSUMER_SECRET}") final String oAuthConsumerSecret,
            @Value("${TWITTER_OAUTH_ACCESS_TOKEN}") final String oAuthAccessToken,
            @Value("${TWITTER_OAUTH_ACCESS_TOKEN_SECRET}") final String oAuthAccessTokenSecret) {
        final twitter4j.conf.Configuration configuration = new ConfigurationBuilder()
            .setOAuthConsumerKey(oAuthConsumerKey)
            .setOAuthConsumerSecret(oAuthConsumerSecret)
            .setOAuthAccessToken(oAuthAccessToken)
            .setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
            .build();
        return new TwitterFactory(configuration).getInstance();
    }

    @Bean
    public TweetFormatUtils tweetFormatUtils(@Qualifier("textTemplateEngine") final ITemplateEngine templateEngine,
            final TweetContext tweetContext) {
        return new TweetFormatUtils(templateEngine, tweetContext);
    }

    @Bean
    public TwitterTimelineEventScheduler twitterTimelineEventScheduler(final DateUtils dateUtils,
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

    @Bean
    public ResourcePatternResolver timelineDataFileResourceResolver() {
        return new PathMatchingResourcePatternResolver(getClass().getClassLoader());
    }

    @Bean
    public TimelineDataLoader timelineDataLoader(final ObjectMapper objectMapper, final ResourcePatternResolver timelineDataFileResourceResolver) {
        return new TimelineDataLoader(objectMapper, timelineDataFileResourceResolver);
    }

    @Bean
    @ConfigurationProperties(prefix = "tweet.context")
    public TweetContext tweetContext() {
        return new TweetContext();
    }
}
