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
package com.jdpgrailsdev.oasis.timeline.controller;


import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** Custom controller that can be used to publish timeline events to Twitter manually. */
@Controller
@RequestMapping("/publish")
public class EventPublisherController {

    private final TwitterTimelineEventScheduler twitterTimelineEventScheduler;

    public EventPublisherController(
            final TwitterTimelineEventScheduler twitterTimelineEventScheduler) {
        this.twitterTimelineEventScheduler = twitterTimelineEventScheduler;
    }

    @RequestMapping("events")
    @ResponseBody
    public void publishEvents() {
        twitterTimelineEventScheduler.publishTimelineTweet();
    }
}
