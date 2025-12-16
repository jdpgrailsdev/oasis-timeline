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

import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.search.TimelineDataSearchResult
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

/** Service that provides operations on [TimelineData]. */
@SuppressFBWarnings("EI_EXPOSE_REP2")
class TimelineDataService(
  private val textSearchService: TextSearchService,
) {
  /**
   * Searches the timeline data for events whose description or title matches the provided query
   * string.
   *
   * @param query The text search query string.
   * @param minScore The minimum score of the match to accept in the final result set
   * @param offset The offset into the result. Used for pagination and defaults to 0.
   * @param limit The number of events to include in the result. Used for pagination and defaults
   *   to 10.
   * @return A potentially paginated list of [TimelineDataSearchResult]s.
   */
  fun search(
    query: String,
    minScore: Float,
    offset: Int = 0,
    limit: Int = 10,
  ): List<TimelineDataSearchResult> = textSearchService.search(query = query, minScore = minScore, offset = offset, limit = limit)
}
