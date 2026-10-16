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

package com.jdpgrailsdev.oasis.timeline.service

import com.jdpgrailsdev.oasis.timeline.controller.SupportController
import com.jdpgrailsdev.oasis.timeline.data.Post
import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SupportMcpServiceTest {
  private lateinit var supportController: SupportController
  private lateinit var service: SupportMcpService

  @BeforeEach
  fun setup() {
    supportController = mockk()
    service = SupportMcpService(supportController = supportController)
  }

  @Test
  fun testGetEvents() {
    val dateString = "2026-10-16"
    val events = listOf(mockk<Post>())
    val target = PostTarget.BLUESKY
    every { supportController.getEvents(any(), any()) }.returns(events)

    val results = supportController.getEvents(dateString = dateString, target = target)
    assertEquals(events.size, results.size)
    verify(exactly = 1) { supportController.getEvents(dateString = dateString, target = target) }
  }
}
