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

import com.github.javafaker.Faker;
import com.jdpgrailsdev.oasis.timeline.data.Post;
import com.jdpgrailsdev.oasis.timeline.data.PostException;
import com.jdpgrailsdev.oasis.timeline.data.PostTarget;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.schedule.PostTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.service.PostPublisherService;
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Calendar;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/** Custom controller that can be used to publish timeline events to social networks manually. */
@Controller
@RequestMapping("/publish")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class EventPublisherController {

  private final PostTimelineEventScheduler postTimelineEventScheduler;

  private final List<PostPublisherService<?>> publishers;

  private final PostFormatUtils postFormatUtils;

  private final Faker faker;

  public EventPublisherController(
      final PostTimelineEventScheduler postTimelineEventScheduler,
      final List<PostPublisherService<?>> publishers,
      final PostFormatUtils postFormatUtils) {
    this.postTimelineEventScheduler = postTimelineEventScheduler;
    this.publishers = publishers;
    this.postFormatUtils = postFormatUtils;
    this.faker = new Faker();
  }

  @RequestMapping("events")
  @ResponseBody
  public void publishAllEvents() {
    postTimelineEventScheduler.publishTimelinePost();
  }

  @RequestMapping("events/{postTarget}")
  @ResponseBody
  public void publishEventsToSocialNetwork(@PathVariable("postTarget") PostTarget postTarget) {
    postTimelineEventScheduler.publishTimelinePost(postTarget);
  }

  @RequestMapping("events/test/{postTarget}")
  @ResponseBody
  public void publishTestEventsToSocialNetwork(
      @PathVariable("postTarget") final PostTarget postTarget,
      @RequestParam(value = "type", required = false) final TimelineDataType type)
      throws PostException {

    final String description =
        "Some text with Test Mention and some hash tags #tag1 and #tag2."
            + "\n"
            + faker.lorem().characters(postTarget.getLimit() * 3);

    final Post post =
        postFormatUtils.generatePost(
            description,
            type != null ? type : TimelineDataType.NOTEWORTHY,
            Calendar.getInstance().get(Calendar.YEAR),
            postTarget);
    publishers.stream()
        .filter((p) -> p.getPostTarget() == postTarget)
        .forEach(p -> p.publish(post));
  }
}
