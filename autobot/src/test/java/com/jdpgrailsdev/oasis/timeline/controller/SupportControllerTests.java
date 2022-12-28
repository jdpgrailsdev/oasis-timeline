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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import com.jdpgrailsdev.oasis.timeline.data.model.mastodon.MastodonStatus;
import com.jdpgrailsdev.oasis.timeline.data.model.twitter.Tweet;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.MastodonFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.TweetFormatUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test suite for the {@link SupportController} class. */
class SupportControllerTests {

  private static final String DATE = "2020-08-04";

  private SupportController controller;

  @BeforeEach
  public void setup() throws PublishedEventException {
    final MastodonFormatUtils mastodonFormatUtils = mock(MastodonFormatUtils.class);
    final MastodonStatus status = mock(MastodonStatus.class);
    final TimelineData timelineData = mock(TimelineData.class);
    final TimelineDataLoader dataLoader = mock(TimelineDataLoader.class);
    final Tweet tweet = mock(Tweet.class);
    final TweetFormatUtils tweetFormatUtils = mock(TweetFormatUtils.class);

    when(dataLoader.getHistory(anyString())).thenReturn(List.of(timelineData, timelineData));
    when(tweetFormatUtils.generateEvent(any(TimelineData.class), anyList())).thenReturn(tweet);
    when(mastodonFormatUtils.generateEvent(any(TimelineData.class), anyList())).thenReturn(status);
    controller =
        new SupportController(new DateUtils(), mastodonFormatUtils, dataLoader, tweetFormatUtils);
  }

  @Test
  @DisplayName("test that when a Twitter request is made, all matching events are returned")
  void testValidTwitterRequest() {
    final List<Tweet> response = controller.getTwitterEvents(DATE);
    assertEquals(response.size(), 2, "expected number of tweets");
  }

  @Test
  @DisplayName("test that when a Mastodon request is made, all matching events are returned")
  void testValidMastodonRequest() {
    final List<MastodonStatus> response = controller.getMastodonEvents(DATE);
    assertEquals(response.size(), 2, "expected number of status updates");
  }

  @Test
  @DisplayName(
      "test that when a request is made but the controller is unable to generate the tweet text,"
          + " the events are left out of the response")
  void testInvalidTwitterRequest() throws PublishedEventException {
    final MastodonFormatUtils mastodonFormatUtils = mock(MastodonFormatUtils.class);
    final TimelineData timelineData = mock(TimelineData.class);
    final TimelineDataLoader dataLoader = mock(TimelineDataLoader.class);
    final TweetFormatUtils tweetFormatUtils = mock(TweetFormatUtils.class);

    when(dataLoader.getHistory(anyString())).thenReturn(List.of(timelineData, timelineData));
    when(tweetFormatUtils.generateEvent(any(TimelineData.class), anyList()))
        .thenThrow(new PublishedEventException("test"));

    controller =
        new SupportController(new DateUtils(), mastodonFormatUtils, dataLoader, tweetFormatUtils);

    final List<Tweet> response = controller.getTwitterEvents(DATE);
    assertEquals(0, response.size(), AssertionMessage.SIZE.toString());
  }

  @Test
  @DisplayName(
      "test that when a request is made but the controller is unable to generate the status text,"
          + " the events are left out of the response")
  void testInvalidMastodonRequest() throws PublishedEventException {
    final MastodonFormatUtils mastodonFormatUtils = mock(MastodonFormatUtils.class);
    final TimelineData timelineData = mock(TimelineData.class);
    final TimelineDataLoader dataLoader = mock(TimelineDataLoader.class);
    final TweetFormatUtils tweetFormatUtils = mock(TweetFormatUtils.class);

    when(dataLoader.getHistory(anyString())).thenReturn(List.of(timelineData, timelineData));
    when(mastodonFormatUtils.generateEvent(any(TimelineData.class), anyList()))
        .thenThrow(new PublishedEventException("test"));

    controller =
        new SupportController(new DateUtils(), mastodonFormatUtils, dataLoader, tweetFormatUtils);

    final List<MastodonStatus> response = controller.getMastodonEvents(DATE);
    assertEquals(0, response.size(), AssertionMessage.SIZE.toString());
  }
}
