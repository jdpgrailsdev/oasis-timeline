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

import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.TweetFormatUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.ITemplateEngine;
import twitter4j.Twitter;

/** Spring configurations for the Twitter API. */
@Configuration
public class TwitterConfiguration {

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
  @SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
  public Twitter twitterApi(
      @Value("${TWITTER_OAUTH_CONSUMER_KEY}") final String oauthConsumerKey,
      @Value("${TWITTER_OAUTH_CONSUMER_SECRET}") final String oauthConsumerSecret,
      @Value("${TWITTER_OAUTH_ACCESS_TOKEN}") final String oauthAccessToken,
      @Value("${TWITTER_OAUTH_ACCESS_TOKEN_SECRET}") final String oauthAccessTokenSecret) {
    return Twitter.newBuilder()
        .oAuthConsumer(oauthConsumerKey, oauthConsumerSecret)
        .oAuthAccessToken(oauthAccessToken, oauthAccessTokenSecret)
        .build();
  }

  /**
   * Defines the {@link TweetFormatUtils} bean.
   *
   * @param templateEngine The template engine used to render the tweet text.
   * @param templateContext The {@link TemplateContext}.
   * @return The {@link TweetFormatUtils} bean.
   */
  @Bean
  public TweetFormatUtils tweetFormatUtils(
      @Qualifier("textTemplateEngine") final ITemplateEngine templateEngine,
      @Qualifier("tweetContext") final TemplateContext templateContext) {
    return new TweetFormatUtils(templateEngine, templateContext);
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
   * Defines the {@link TemplateContext} bean.
   *
   * @return The {@link TemplateContext} bean.
   */
  @Bean
  @ConfigurationProperties(prefix = "tweet.context")
  public TemplateContext tweetContext() {
    return new TemplateContext();
  }
}
