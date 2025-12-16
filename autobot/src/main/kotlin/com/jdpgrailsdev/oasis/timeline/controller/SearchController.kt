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
import com.jdpgrailsdev.oasis.timeline.service.TimelineDataService
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

/** Custom controller that exposes search functionality over timeline event data. */
@Controller
@RequestMapping("/search")
@SuppressFBWarnings("BC_BAD_CAST_TO_ABSTRACT_COLLECTION")
class SearchController(
  private val timelineDataService: TimelineDataService,
) {
  @RequestMapping("timeline")
  @ResponseBody
  fun searchTimelineData(
    @RequestParam("query") query: String,
    @RequestParam("minScore", required = false, defaultValue = "0.5f") minScore: Float,
    @RequestParam("limit", required = false, defaultValue = "50") limit: Int,
    @RequestParam("offset", required = false, defaultValue = "0") offset: Int,
  ): List<TimelineData> =
    timelineDataService
      .search(query = query, minScore = minScore, limit = limit, offset = offset)
      .map { it.timelineData }
}
