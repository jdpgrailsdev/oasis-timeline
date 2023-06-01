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
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetException;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TweetsApi.APItweetsRecentSearchRequest;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Get2TweetsSearchRecentResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SupportControllerTests {

  private SupportController controller;

  @BeforeEach
  public void setup() throws TweetException {
    final TimelineData timelineData = mock(TimelineData.class);
    final TimelineDataLoader dataLoader = mock(TimelineDataLoader.class);
    final Tweet tweet = mock(Tweet.class);
    final TweetFormatUtils tweetFormatUtils = mock(TweetFormatUtils.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);

    when(dataLoader.getHistory(anyString())).thenReturn(List.of(timelineData, timelineData));
    when(tweetFormatUtils.generateTweet(any(TimelineData.class), anyList())).thenReturn(tweet);
    controller = new SupportController(new DateUtils(), dataLoader, tweetFormatUtils, twitterApi);
  }

  @Test
  @DisplayName("test that when a request is made, all matching events are returned")
  void testValidRequest() {
    final String date = "2020-08-04";
    final List<Tweet> response = controller.getEvents(date);
    assertEquals(response.size(), 2, "expected number of tweets");
  }

  @Test
  @DisplayName(
      "test that when a request is made but the controller is unable to generate the tweet text,"
          + " the events are left out of the response")
  void testInvalidRequest() throws TweetException {
    final TimelineData timelineData = mock(TimelineData.class);
    final TimelineDataLoader dataLoader = mock(TimelineDataLoader.class);
    final TweetFormatUtils tweetFormatUtils = mock(TweetFormatUtils.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);

    when(dataLoader.getHistory(anyString())).thenReturn(List.of(timelineData, timelineData));
    when(tweetFormatUtils.generateTweet(any(TimelineData.class), anyList()))
        .thenThrow(new TweetException("test"));

    controller = new SupportController(new DateUtils(), dataLoader, tweetFormatUtils, twitterApi);

    final String date = "2020-08-04";
    final List<Tweet> response = controller.getEvents(date);
    assertEquals(0, response.size(), AssertionMessage.SIZE.toString());
  }

  @Test
  void testGetRecentTweets() throws ApiException {
    final String tweetText = "Hello world!";
    final TimelineDataLoader dataLoader = mock(TimelineDataLoader.class);
    final TweetFormatUtils tweetFormatUtils = mock(TweetFormatUtils.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TweetsApi tweetsApi = mock(TweetsApi.class);
    final APItweetsRecentSearchRequest apiTweetsRecentSearchRequest = mock(APItweetsRecentSearchRequest.class);
    final Get2TweetsSearchRecentResponse get2TweetsSearchRecentResponse = mock(Get2TweetsSearchRecentResponse.class);
    final com.twitter.clientlib.model.Tweet tweet = mock(com.twitter.clientlib.model.Tweet.class);

    when(tweet.getText()).thenReturn(tweetText);
    when(get2TweetsSearchRecentResponse.getData()).thenReturn(List.of(tweet));
    when(apiTweetsRecentSearchRequest.execute()).thenReturn(get2TweetsSearchRecentResponse);
    when(tweetsApi.tweetsRecentSearch(anyString())).thenReturn(apiTweetsRecentSearchRequest);
    when(twitterApi.tweets()).thenReturn(tweetsApi);

    controller = new SupportController(new DateUtils(), dataLoader, tweetFormatUtils, twitterApi);

    final List<String> recentTweets = controller.getRecentTweets();
    assertEquals(1, recentTweets.size());
    assertEquals(tweetText, recentTweets.get(0));
  }
}
