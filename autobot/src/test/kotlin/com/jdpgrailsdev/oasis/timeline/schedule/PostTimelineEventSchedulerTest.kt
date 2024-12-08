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
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okio.IOException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class PostTimelineEventSchedulerTest {
  @Test
  fun testSchedulePublishesEvents() {
    val today = "January 1"
    val dateUtils = mockk<DateUtils> { every { today() } returns today }
    val timer =
      mockk<Timer> {
        every { record(any<Runnable>()) } answers { (invocation.args.first() as Runnable).run() }
      }
    val meterRegistry = mockk<MeterRegistry> { every { timer(any()) } returns timer }
    val timelineData = (1..10).map { mockk<TimelineData>() }
    val timelineDataLoader =
      mockk<TimelineDataLoader> { every { getHistory(today) } returns timelineData }
    val publisher1 =
      mockk<PostPublisherService<String>> {
        every { getPostTarget() } returns PostTarget.BLUESKY
        every { publishTimelineEvents(timelineData) } returns Unit
      }
    val publisher2 =
      mockk<PostPublisherService<String>> {
        every { getPostTarget() } returns PostTarget.TWITTER
        every { publishTimelineEvents(timelineData) } returns Unit
      }
    val publishers = listOf(publisher1, publisher2)

    val scheduler =
      PostTimelineEventScheduler(
        dateUtils = dateUtils,
        meterRegistry = meterRegistry,
        publishers = publishers,
        timelineDataLoader = timelineDataLoader,
      )

    scheduler.publishTimelinePost()

    verify(exactly = 1) { timelineDataLoader.getHistory(today) }
    verify(exactly = 1) { publisher1.publishTimelineEvents(timelineData) }
    verify(exactly = 1) { publisher2.publishTimelineEvents(timelineData) }
  }

  @Test
  fun testSchedulePublishesEventsSpecificPostTarget() {
    val target = PostTarget.BLUESKY
    val today = "January 1"
    val dateUtils = mockk<DateUtils> { every { today() } returns today }
    val timer =
      mockk<Timer> {
        every { record(any<Runnable>()) } answers { (invocation.args.first() as Runnable).run() }
      }
    val meterRegistry = mockk<MeterRegistry> { every { timer(any()) } returns timer }
    val timelineData = (1..10).map { mockk<TimelineData>() }
    val timelineDataLoader =
      mockk<TimelineDataLoader> { every { getHistory(today) } returns timelineData }
    val publisher1 =
      mockk<PostPublisherService<String>> {
        every { getPostTarget() } returns PostTarget.BLUESKY
        every { publishTimelineEvents(timelineData) } returns Unit
      }
    val publisher2 =
      mockk<PostPublisherService<String>> {
        every { getPostTarget() } returns PostTarget.TWITTER
        every { publishTimelineEvents(timelineData) } returns Unit
      }
    val publishers = listOf(publisher1, publisher2)

    val scheduler =
      PostTimelineEventScheduler(
        dateUtils = dateUtils,
        meterRegistry = meterRegistry,
        publishers = publishers,
        timelineDataLoader = timelineDataLoader,
      )

    scheduler.publishTimelinePost(postTarget = target)

    verify(exactly = 1) { timelineDataLoader.getHistory(today) }
    verify(exactly = 1) { publisher1.publishTimelineEvents(timelineData) }
    verify(exactly = 0) { publisher2.publishTimelineEvents(timelineData) }
  }

  @Test
  fun testPublishExceptionHandling() {
    val today = "January 1"
    val dateUtils = mockk<DateUtils> { every { today() } returns today }
    val timer =
      mockk<Timer> {
        every { record(any<Runnable>()) } answers { (invocation.args.first() as Runnable).run() }
      }
    val meterRegistry = mockk<MeterRegistry> { every { timer(any()) } returns timer }
    val timelineData = (1..10).map { mockk<TimelineData>() }
    val timelineDataLoader =
      mockk<TimelineDataLoader> { every { getHistory(today) } returns timelineData }
    val publisher1 =
      mockk<PostPublisherService<String>> {
        every { getPostTarget() } returns PostTarget.BLUESKY
        every { publishTimelineEvents(timelineData) } throws IOException("text")
      }
    val publisher2 =
      mockk<PostPublisherService<String>> {
        every { getPostTarget() } returns PostTarget.TWITTER
        every { publishTimelineEvents(timelineData) } returns Unit
      }
    val publishers = listOf(publisher1, publisher2)

    val scheduler =
      PostTimelineEventScheduler(
        dateUtils = dateUtils,
        meterRegistry = meterRegistry,
        publishers = publishers,
        timelineDataLoader = timelineDataLoader,
      )

    assertDoesNotThrow { scheduler.publishTimelinePost() }
    verify(exactly = 1) { publisher2.publishTimelineEvents(timelineData) }
  }
}
