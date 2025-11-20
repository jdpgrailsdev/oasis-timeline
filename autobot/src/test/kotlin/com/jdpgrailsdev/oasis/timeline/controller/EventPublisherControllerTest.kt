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

package com.jdpgrailsdev.oasis.timeline.controller

import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.schedule.PostTimelineEventScheduler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class EventPublisherControllerTest {
  @Test
  @DisplayName(
    (
      "test that when the controller is invoked, the underlying scheduler is called to publish all" +
        " events"
    ),
  )
  fun testPublishingAllEvents() {
    val scheduler: PostTimelineEventScheduler =
      mockk {
        every { publishTimelinePost() } returns Unit
      }
    val controller = EventPublisherController(scheduler)
    controller.publishAllEvents()

    verify(exactly = 1) { scheduler.publishTimelinePost() }
  }

  @ParameterizedTest
  @EnumSource(PostTarget::class)
  @DisplayName(
    (
      "test that when the controller is invoked, the underlying scheduler is called to publish to" +
        " specific social network"
    ),
  )
  fun testPublishingSpecificEvents(target: PostTarget) {
    val scheduler: PostTimelineEventScheduler =
      mockk {
        every { publishTimelinePost(target) } returns Unit
      }
    val controller = EventPublisherController(scheduler)

    controller.publishEventsToSocialNetwork(target)

    verify(exactly = 1) { scheduler.publishTimelinePost(target) }
  }
}
