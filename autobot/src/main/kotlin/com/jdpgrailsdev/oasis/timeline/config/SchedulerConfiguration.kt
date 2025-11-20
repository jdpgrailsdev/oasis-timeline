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

import com.fasterxml.jackson.databind.ObjectMapper
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.schedule.Oauth2Scheduler
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.scheduling.annotation.EnableScheduling

/** Spring configuration for scheduler beans. */
@Suppress("UNUSED")
@Configuration
@EnableScheduling
class SchedulerConfiguration {
  /**
   * Defines the [DateUtils] bean.
   *
   * @return The [DateUtils] bean.
   */
  @Bean fun dateUtils(): DateUtils = DateUtils()

  /**
   * Defines the [Oauth2Scheduler] bean.
   *
   * @param meterRegistry Micrometer [MeterRegistry] bean.
   * @param twitterApiUtils The [TwitterApiUtils] bean.
   * @return The [Oauth2Scheduler] bean.
   */
  @Bean
  fun oauth2Scheduler(
    meterRegistry: MeterRegistry,
    twitterApiUtils: TwitterApiUtils,
  ): Oauth2Scheduler = Oauth2Scheduler(meterRegistry, twitterApiUtils)

  /**
   * Defines the [ResourcePatternResolver] bean used to find and load the data file.
   *
   * @return The [ResourcePatternResolver] bean.
   */
  @Bean
  fun timelineDataFileResourceResolver(): ResourcePatternResolver =
    PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader())

  /**
   * Defines the [TimelineDataLoader] bean.
   *
   * @param objectMapper A Jackson [ObjectMapper] instance.
   * @param timelineDataFileResourceResolver The data file resource resolver bean.
   * @return The [TimelineDataLoader] bean.
   */
  @Bean
  fun timelineDataLoader(
    objectMapper: ObjectMapper,
    timelineDataFileResourceResolver: ResourcePatternResolver,
  ): TimelineDataLoader = TimelineDataLoader(objectMapper, timelineDataFileResourceResolver)
}
