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

import com.jdpgrailsdev.oasis.timeline.search.TimelineDataSearchResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TimelineDataSearchServiceTest {
  private lateinit var searchService: TimelineDataService
  private lateinit var textSearchService: TextSearchService

  @BeforeEach
  fun setup() {
    textSearchService =
      mockk {
        every { search(any(), any(), any(), any()) } returns listOf(mockk<TimelineDataSearchResult>())
      }
    searchService = TimelineDataService(textSearchService = textSearchService)
  }

  @Test
  fun testSearch() {
    val minimumScore = 1.0f
    val result = searchService.search("a text search query", minimumScore)
    assertEquals(1, result.size)
    verify(exactly = 1) { textSearchService.search(any(), any(), any(), any()) }
  }
}
