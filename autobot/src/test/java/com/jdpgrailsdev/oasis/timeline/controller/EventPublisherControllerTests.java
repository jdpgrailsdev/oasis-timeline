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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jdpgrailsdev.oasis.timeline.data.PostTarget;
import com.jdpgrailsdev.oasis.timeline.schedule.PostTimelineEventScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventPublisherControllerTests {

  @Test
  @DisplayName(
      "test that when the controller is invoked, the underlying scheduler is called to publish all"
          + " events")
  void testPublishingAllEvents() {
    final PostTimelineEventScheduler scheduler = mock(PostTimelineEventScheduler.class);
    final EventPublisherController controller = new EventPublisherController(scheduler);

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
    final EventPublisherController controller = new EventPublisherController(scheduler);
    final PostTarget target = PostTarget.BLUESKY;

    doNothing().when(scheduler).publishTimelinePost(target);

    controller.publishEventsToSocialNetwork(target);

    verify(scheduler, times(1)).publishTimelinePost(target);
  }
}
