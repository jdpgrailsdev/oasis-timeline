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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/** Custom controller that can be used to publish timeline events to social networks manually. */
@Controller
@RequestMapping("/publish")
@SuppressFBWarnings("EI_EXPOSE_REP2")
class EventPublisherController(
  private val postTimelineEventScheduler: PostTimelineEventScheduler,
) {
  @RequestMapping("events")
  @ResponseBody
  fun publishAllEvents() {
    postTimelineEventScheduler.publishTimelinePost()
  }

  @RequestMapping("events/{postTarget}")
  @ResponseBody
  fun publishEventsToSocialNetwork(
    @PathVariable("postTarget") postTarget: PostTarget,
  ) {
    postTimelineEventScheduler.publishTimelinePost(postTarget)
  }
}
