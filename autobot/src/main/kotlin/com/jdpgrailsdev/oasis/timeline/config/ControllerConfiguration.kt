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

package com.jdpgrailsdev.oasis.timeline.config

import com.github.javafaker.Faker
import com.github.scribejava.core.pkce.PKCE
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.controller.EventPublisherController
import com.jdpgrailsdev.oasis.timeline.controller.OAuth2Controller
import com.jdpgrailsdev.oasis.timeline.controller.SearchController
import com.jdpgrailsdev.oasis.timeline.controller.StatusController
import com.jdpgrailsdev.oasis.timeline.controller.SupportController
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.schedule.PostTimelineEventScheduler
import com.jdpgrailsdev.oasis.timeline.service.PostPublisherService
import com.jdpgrailsdev.oasis.timeline.service.TimelineDataService
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.auth.TwitterOAuth20Service
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Spring configurations for controllers. */
@Suppress("UNUSED")
@Configuration
@EnableAutoConfiguration
class ControllerConfiguration {
  /**
   * Defines the controller that can be used to publish timeline events to Twitter manually.
   *
   * @param postTimelineEventScheduler The [PostTimelineEventScheduler] bean.
   * @return The [EventPublisherController] bean.
   */
  @Bean
  fun eventPublisherController(postTimelineEventScheduler: PostTimelineEventScheduler): EventPublisherController =
    EventPublisherController(postTimelineEventScheduler)

  /**
   * Defines the status controller used to verify that the application is running.
   *
   * @return The [StatusController] bean.
   */
  @Bean fun statusController(): StatusController = StatusController()

  /**
   * Defines the support controller that contains various endpoints used to provide debug or
   * diagnostic information.
   *
   * @param blueSkyClient The [BlueSkyClient] bean.
   * @param dateUtils The [DateUtils] bean.
   * @param faker The [Faker] bean.
   * @param postFormatUtils The [PostFormatUtils] bean.
   * @param publishers The list of [PostPublisherService] beans.
   * @param timelineDataLoader The [TimelineDataLoader] bean.
   * @param twitterApiUtils The [TwitterApiUtils] bean
   * @return The [SupportController] bean.
   */
  @Bean
  fun supportController(
    blueSkyClient: BlueSkyClient,
    dateUtils: DateUtils,
    faker: Faker,
    postFormatUtils: PostFormatUtils,
    publishers: List<PostPublisherService<*>>,
    timelineDataLoader: TimelineDataLoader,
    twitterApiUtils: TwitterApiUtils,
  ): SupportController =
    SupportController(
      blueSkyClient,
      dateUtils,
      faker,
      postFormatUtils,
      publishers,
      timelineDataLoader,
      twitterApiUtils,
    )

  /**
   * Defines the OAuth2 controller that handles authorization.
   *
   * @param pkce The [PKCE] bean.
   * @param twitterApiUtils The [TwitterApiUtils] bean.
   * @param twitterOAuth2Service The [TwitterOAuth20Service] bean.
   * @return The [OAuth2Controller] bean.
   */
  @Bean
  fun oAuth2Controller(
    pkce: PKCE,
    twitterApiUtils: TwitterApiUtils,
    twitterOAuth2Service: TwitterOAuth20Service,
  ): OAuth2Controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth2Service)

  @Bean
  fun searchController(timelineDataService: TimelineDataService): SearchController = SearchController(timelineDataService)
}
