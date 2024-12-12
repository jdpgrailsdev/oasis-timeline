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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpgrailsdev.oasis.timeline.data.Post;
import com.jdpgrailsdev.oasis.timeline.data.PostException;
import com.jdpgrailsdev.oasis.timeline.data.PostTarget;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.schedule.PostTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.service.PostPublisherService;
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventPublisherControllerTests {

  @Test
  @DisplayName(
      "test that when the controller is invoked, the underlying scheduler is called to publish all"
          + " events")
  void testPublishingAllEvents() {
    final PostTimelineEventScheduler scheduler = mock(PostTimelineEventScheduler.class);
    final PostPublisherService<?> publisher = mock(PostPublisherService.class);
    final List<PostPublisherService<?>> publishers = List.of(publisher);
    final PostFormatUtils postFormatUtils = mock(PostFormatUtils.class);
    final EventPublisherController controller =
        new EventPublisherController(scheduler, publishers, postFormatUtils);

    doNothing().when(scheduler).publishTimelinePost();

    controller.publishAllEvents();

    verify(scheduler, times(1)).publishTimelinePost();
  }

  @Test
  @DisplayName(
      "test that when the controller is invoked, the underlying scheduler is called to publish to"
          + " specific social network")
  void testPublishingSpecificEvents() {
    final PostTimelineEventScheduler scheduler = mock(PostTimelineEventScheduler.class);
    final PostPublisherService<?> publisher = mock(PostPublisherService.class);
    final List<PostPublisherService<?>> publishers = List.of(publisher);
    final PostFormatUtils postFormatUtils = mock(PostFormatUtils.class);
    final EventPublisherController controller =
        new EventPublisherController(scheduler, publishers, postFormatUtils);
    final PostTarget target = PostTarget.BLUESKY;

    doNothing().when(scheduler).publishTimelinePost(target);

    controller.publishEventsToSocialNetwork(target);

    verify(scheduler, times(1)).publishTimelinePost(target);
  }

  @Test
  @DisplayName("test that when a test event is published, the underlying publish is invoked")
  void testPublishingTestEvent() throws PostException {
    final PostTimelineEventScheduler scheduler = mock(PostTimelineEventScheduler.class);
    final PostPublisherService<?> publisher = mock(PostPublisherService.class);
    final List<PostPublisherService<?>> publishers = List.of(publisher);
    final PostFormatUtils postFormatUtils = mock(PostFormatUtils.class);
    final Post post = mock(Post.class);
    final PostTarget postTarget = PostTarget.BLUESKY;

    when(publisher.getPostTarget()).thenReturn(postTarget);
    when(postFormatUtils.generatePost(anyString(), any(), anyInt(), any())).thenReturn(post);

    final EventPublisherController controller =
        new EventPublisherController(scheduler, publishers, postFormatUtils);

    controller.publishTestEventsToSocialNetwork(postTarget, null);

    verify(publisher, times(1)).publish(post);
  }

  @Test
  @DisplayName(
      "test that when a test event is published with a specific timeline data type, the underlying"
          + " publish is invoked")
  void testPublishingTestEventWithSpecificTimelineDataType() throws PostException {
    final PostTimelineEventScheduler scheduler = mock(PostTimelineEventScheduler.class);
    final PostPublisherService<?> publisher = mock(PostPublisherService.class);
    final List<PostPublisherService<?>> publishers = List.of(publisher);
    final PostFormatUtils postFormatUtils = mock(PostFormatUtils.class);
    final Post post = mock(Post.class);
    final PostTarget postTarget = PostTarget.BLUESKY;

    when(publisher.getPostTarget()).thenReturn(postTarget);
    when(postFormatUtils.generatePost(anyString(), any(), anyInt(), any())).thenReturn(post);

    final EventPublisherController controller =
        new EventPublisherController(scheduler, publishers, postFormatUtils);

    controller.publishTestEventsToSocialNetwork(postTarget, TimelineDataType.VIDEOS);

    verify(publisher, times(1)).publish(post);
  }
}
