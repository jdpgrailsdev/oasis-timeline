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

import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyFacetType
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.schedule.PostTimelineEventScheduler
import com.jdpgrailsdev.oasis.timeline.service.BlueSkyPostPublisherService
import com.jdpgrailsdev.oasis.timeline.service.MastodonPostPublisherService
import com.jdpgrailsdev.oasis.timeline.service.PostPublisherService
import com.jdpgrailsdev.oasis.timeline.service.TwitterPostPublisherService
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.MastodonApiUtils
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.thymeleaf.ITemplateEngine

@Configuration
@EnableScheduling
class PostConfiguration {
  /**
   * Defines the [PostTimelineEventScheduler] bean.
   *
   * @param dateUtils [DateUtils] used to format date for timeline data event selection.
   * @param meterRegistry Micrometer [MeterRegistry] bean.
   * @param publishers The list of available [PostPublisherService]s used to publish posts.
   * @param timelineDataLoader [TimelineDataLoader] instance used to load events to publish.
   * @return The [PostTimelineEventScheduler] bean.
   */
  @Bean
  fun postTimelineEventScheduler(
    dateUtils: DateUtils,
    meterRegistry: MeterRegistry,
    publishers: List<PostPublisherService<*>>,
    timelineDataLoader: TimelineDataLoader,
  ): PostTimelineEventScheduler =
    PostTimelineEventScheduler(
      dateUtils = dateUtils,
      meterRegistry = meterRegistry,
      publishers = publishers,
      timelineDataLoader = timelineDataLoader,
    )

  /**
   * Defines the [PostFormatUtils] bean.
   *
   * @param templateEngine The template engine used to render the post text.
   * @param socialContexts The list of configured [SocialContext]s.
   * @return The [PostFormatUtils] bean.
   */
  @Bean
  fun postFormatUtils(
    @Qualifier("textTemplateEngine") templateEngine: ITemplateEngine,
    socialContexts: List<SocialContext>,
  ): PostFormatUtils = PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = socialContexts)

  @Bean
  fun blueSkyPublisherService(
    blueSkyClient: BlueSkyClient,
    dateUtils: DateUtils,
    meterRegistry: MeterRegistry,
    postFormatUtils: PostFormatUtils,
    timelineDataLoader: TimelineDataLoader,
    @Qualifier("blueSkyResolverMap")
    blueSkyResolverMap: Map<BlueSkyFacetType, (mention: String) -> String>,
  ): BlueSkyPostPublisherService =
    BlueSkyPostPublisherService(
      blueSkyClient = blueSkyClient,
      blueSkyResolverMap = blueSkyResolverMap,
      dateUtils = dateUtils,
      meterRegistry = meterRegistry,
      postFormatUtils = postFormatUtils,
      timelineDataLoader = timelineDataLoader,
    )

  @Bean
  fun mastodonPublisherService(
    mastodonApiUtils: MastodonApiUtils,
    dateUtils: DateUtils,
    meterRegistry: MeterRegistry,
    postFormatUtils: PostFormatUtils,
    timelineDataLoader: TimelineDataLoader,
  ): MastodonPostPublisherService =
    MastodonPostPublisherService(
      mastodonApiUtils = mastodonApiUtils,
      dateUtils = dateUtils,
      meterRegistry = meterRegistry,
      postFormatUtils = postFormatUtils,
      timelineDataLoader = timelineDataLoader,
    )

  @Bean
  fun twitterPublisherService(
    dateUtils: DateUtils,
    meterRegistry: MeterRegistry,
    postFormatUtils: PostFormatUtils,
    timelineDataLoader: TimelineDataLoader,
    twitterApiUtils: TwitterApiUtils,
  ): TwitterPostPublisherService =
    TwitterPostPublisherService(
      dateUtils = dateUtils,
      meterRegistry = meterRegistry,
      postFormatUtils = postFormatUtils,
      timelineDataLoader = timelineDataLoader,
      twitterApiUtils = twitterApiUtils,
    )
}
