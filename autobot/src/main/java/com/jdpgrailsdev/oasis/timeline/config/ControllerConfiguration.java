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

import com.github.scribejava.core.pkce.PKCE;
import com.jdpgrailsdev.oasis.timeline.controller.EventPublisherController;
import com.jdpgrailsdev.oasis.timeline.controller.OAuth2Controller;
import com.jdpgrailsdev.oasis.timeline.controller.StatusController;
import com.jdpgrailsdev.oasis.timeline.controller.SupportController;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.Oauth2Scheduler;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring configurations for controllers. */
@Configuration
@EnableAutoConfiguration
public class ControllerConfiguration {

  /**
   * Defines the controller that can be used to publish timeline events to Twitter manually.
   *
   * @param twitterTimelineEventScheduler The {@link TwitterTimelineEventScheduler} bean.
   * @return The {@link EventPublisherController} bean.
   */
  @Bean
  public EventPublisherController eventPublisherController(
      final TwitterTimelineEventScheduler twitterTimelineEventScheduler) {
    return new EventPublisherController(twitterTimelineEventScheduler);
  }

  /**
   * Defines the status controller used to verify that the application is running.
   *
   * @return The {@link StatusController} bean.
   */
  @Bean
  public StatusController statusController() {
    return new StatusController();
  }

  /**
   * Defines the support controller that contains various endpoints used to provide debug or
   * diagnostic information.
   *
   * @param dateUtils The {@link DateUtils} bean.
   * @param timelineDataLoader The {@link TimelineDataLoader} bean.
   * @param tweetFormatUtils The {@link TweetFormatUtils} bean.
   * @param twitterApiUtils The {@link TwitterApiUtils} bean
   * @return The {@link SupportController} bean.
   */
  @Bean
  public SupportController supportController(
      final DateUtils dateUtils,
      final TimelineDataLoader timelineDataLoader,
      final TweetFormatUtils tweetFormatUtils,
      final TwitterApiUtils twitterApiUtils) {
    return new SupportController(dateUtils, timelineDataLoader, tweetFormatUtils, twitterApiUtils);
  }

  /**
   * Defines the OAuth2 controller that handles authorization.
   *
   * @param pkce The {@link PKCE} bean.
   * @param twitterApiUtils The {@link TwitterApiUtils} bean.
   * @param twitterOAuth2Service The {@link TwitterOAuth20Service} bean.
   * @return The {@link OAuth2Controller} bean.
   */
  @Bean
  @SuppressWarnings({"AbbreviationAsWordInName", "MethodName"})
  public OAuth2Controller oAuth2Controller(
      final Oauth2Scheduler oauth2Scheduler,
      final PKCE pkce,
      final TwitterApiUtils twitterApiUtils,
      final TwitterOAuth20Service twitterOAuth2Service) {
    return new OAuth2Controller(oauth2Scheduler, pkce, twitterApiUtils, twitterOAuth2Service);
  }
}
