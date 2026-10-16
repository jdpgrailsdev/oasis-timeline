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
import org.springframework.ai.mcp.annotation.McpTool
import org.springframework.ai.mcp.annotation.McpToolParam
import java.time.format.DateTimeFormatter

/** Collection of MCP tools to provide debug or diagnostic information. */
class SupportMcpService(
  private val supportController: SupportController,
) {
  /**
   * Generates the events for a given date.
   *
   * @param dateString A date string in [DateTimeFormatter.ISO_LOCAL_DATE] format.
   * @param target The target social network to receive the post.
   * @return The list of generated events for the provided data or an empty list if no events exist.
   */
  @McpTool(
    name = "get_events",
    description = "Retrieves the events for the given formatted date and social network",
  )
  fun getEvents(
    @McpToolParam(
      description = "A date string in ISO-8601 extended local date format",
      required = true,
    )
    dateString: String,
    @McpToolParam(description = "The target social network to receive the post", required = true)
    target: PostTarget,
  ): List<Post> = supportController.getEvents(dateString = dateString, target = target)
}
