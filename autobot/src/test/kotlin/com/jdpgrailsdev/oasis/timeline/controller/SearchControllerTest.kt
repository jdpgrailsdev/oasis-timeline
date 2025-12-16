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

import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.search.TimelineDataSearchResult
import com.jdpgrailsdev.oasis.timeline.service.TimelineDataService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SearchControllerTest {
  @Test
  fun testTimelineDataSearch() {
    val query = "some query tokens"
    val minScore = 1.0f
    val limit = 50
    val offset = 5
    val searchResults = listOf(TimelineDataSearchResult(5.0f, mockk<TimelineData>(relaxed = true)))
    val timelineDataService: TimelineDataService =
      mockk {
        every { search(query = query, minScore = minScore, offset = offset, limit = limit) } returns
          searchResults
      }
    val controller = SearchController(timelineDataService)

    val results = controller.searchTimelineData(query, minScore, limit, offset)
    assertEquals(1, results.size)
  }
}
