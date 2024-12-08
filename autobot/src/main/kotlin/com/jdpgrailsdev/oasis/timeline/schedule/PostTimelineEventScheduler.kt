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

package com.jdpgrailsdev.oasis.timeline.schedule

import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.service.PostPublisherService
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.scheduling.annotation.Scheduled

const val PUBLISH_TIMER_NAME = "publishTimelineTweet"

private val logger = KotlinLogging.logger {}

@SuppressFBWarnings(value = ["EI_EXPOSE_REP2", "BC_BAD_CAST_TO_ABSTRACT_COLLECTION"])
class PostTimelineEventScheduler(
  private val dateUtils: DateUtils,
  private val meterRegistry: MeterRegistry,
  private val publishers: List<PostPublisherService<*>>,
  private val timelineDataLoader: TimelineDataLoader,
) {
  /**
   * Publishes posts to each configured social network for each timeline event associated with
   * today's date.
   */
  @Scheduled(cron = "0 30 5 * * *")
  fun publishTimelinePost() {
    val timelineData = generateTimelineEvents()
    logger.debug { "Publishing ${timelineData.size} event(s)..." }
    meterRegistry
      .timer(PUBLISH_TIMER_NAME)
      .record(
        Runnable {
          publishers.forEach { publisher ->
            publishTimelineEvents(publisher = publisher, timelineData = timelineData)
          }
          logger.debug {
            "Execution of scheduled publish of ${timelineData.size} timeline event(s) to ${publishers.size} publisher(s) completed."
          }
        },
      )
  }

  /**
   * Publishes posts to the specified post target.
   *
   * @param postTarget The [PostTarget] social network.
   */
  fun publishTimelinePost(postTarget: PostTarget) {
    val timelineData = generateTimelineEvents()
    publishers
      .filter { it.getPostTarget() == postTarget }
      .forEach { publisher ->
        publishTimelineEvents(publisher = publisher, timelineData = timelineData)
      }
  }

  private fun generateTimelineEvents(): List<TimelineData> {
    val today: String = dateUtils.today()
    logger.debug { "Fetching timeline events for today's date $today..." }
    return timelineDataLoader.getHistory(today)
  }

  private fun publishTimelineEvents(
    publisher: PostPublisherService<*>,
    timelineData: List<TimelineData>,
  ) {
    val publisherTarget = publisher.getPostTarget().displayName()
    try {
      logger.debug {
        "Attempting to publish ${timelineData.size} timeline event(s) to publisher target $publisherTarget..."
      }
      publisher.publishTimelineEvents(timelineData = timelineData)
      logger.debug {
        "Execution of publish of ${timelineData.size} timeline event(s) to publisher target $publisherTarget successful."
      }
    } catch (e: Exception) {
      logger.error(e) {
        "Unable to publish ${timelineData.size} timeline events to publisher target $publisherTarget."
      }
    }
  }
}
