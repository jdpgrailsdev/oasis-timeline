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

import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient;
import com.jdpgrailsdev.oasis.timeline.data.Post;
import com.jdpgrailsdev.oasis.timeline.data.PostException;
import com.jdpgrailsdev.oasis.timeline.data.PostTarget;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TweetsApi.APItweetsRecentSearchRequest;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.api.UsersApi;
import com.twitter.clientlib.api.UsersApi.APIfindMyUserRequest;
import com.twitter.clientlib.model.Get2TweetsSearchRecentResponse;
import com.twitter.clientlib.model.Get2UsersMeResponse;
import com.twitter.clientlib.model.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SupportControllerTests {

  private BlueSkyClient blueSkyClient;
  private TimelineData timelineData;
  private TimelineDataLoader dataLoader;
  private PostFormatUtils postFormatUtils;
  private TwitterApi twitterApi;
  private SupportController controller;

  @BeforeEach
  public void setup() throws PostException {
    blueSkyClient = mock(BlueSkyClient.class);
    timelineData = mock(TimelineData.class);
    dataLoader = mock(TimelineDataLoader.class);
    final Post post = mock(Post.class);
    postFormatUtils = mock(PostFormatUtils.class);
    twitterApi = mock(TwitterApi.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);

    when(dataLoader.getHistory(anyString())).thenReturn(List.of(timelineData, timelineData));
    when(postFormatUtils.generatePost(any(TimelineData.class), anyList(), any(PostTarget.class)))
        .thenReturn(post);
    controller =
        new SupportController(
            blueSkyClient, new DateUtils(), dataLoader, postFormatUtils, twitterApiUtils);
    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);
  }

  @ParameterizedTest
  @EnumSource(PostTarget.class)
  @DisplayName("test that when a request is made, all matching events are returned")
  void testValidRequest(final PostTarget postTarget) {
    final String date = "2020-08-04";
    final List<Post> response = controller.getEvents(date, postTarget);
    assertEquals(2, response.size());
  }

  @ParameterizedTest
  @EnumSource(PostTarget.class)
  @DisplayName(
      "test that when a request is made but the controller is unable to generate the tweet text,"
          + " the events are left out of the response")
  void testInvalidRequest(final PostTarget postTarget) throws PostException {
    when(dataLoader.getHistory(anyString())).thenReturn(List.of(timelineData, timelineData));
    when(postFormatUtils.generatePost(any(TimelineData.class), anyList(), any(PostTarget.class)))
        .thenThrow(new PostException("test"));

    final String date = "2020-08-04";
    final List<Post> response = controller.getEvents(date, postTarget);
    assertEquals(0, response.size());
  }

  @Test
  void testGetRecentBlueSkyPosts() {
    final String postText = "Hello world!";
    when(blueSkyClient.getPosts()).thenReturn(List.of(postText));

    final List<String> recentPosts = controller.getRecentBlueSkyPosts();
    assertEquals(1, recentPosts.size());
    assertEquals(postText, recentPosts.getFirst());
  }

  @Test
  void testGetRecentTweets() throws ApiException {
    final String tweetText = "Hello world!";
    final TweetsApi tweetsApi = mock(TweetsApi.class);
    final APItweetsRecentSearchRequest apiTweetsRecentSearchRequest =
        mock(APItweetsRecentSearchRequest.class);
    final Get2TweetsSearchRecentResponse get2TweetsSearchRecentResponse =
        mock(Get2TweetsSearchRecentResponse.class);
    final com.twitter.clientlib.model.Tweet tweet = mock(com.twitter.clientlib.model.Tweet.class);

    when(tweet.getText()).thenReturn(tweetText);
    when(get2TweetsSearchRecentResponse.getData()).thenReturn(List.of(tweet));
    when(apiTweetsRecentSearchRequest.execute()).thenReturn(get2TweetsSearchRecentResponse);
    when(tweetsApi.tweetsRecentSearch(anyString())).thenReturn(apiTweetsRecentSearchRequest);
    when(twitterApi.tweets()).thenReturn(tweetsApi);

    final List<String> recentTweets = controller.getRecentTweets();
    assertEquals(1, recentTweets.size());
    assertEquals(tweetText, recentTweets.getFirst());
  }

  @Test
  void testGetUser() throws ApiException {
    final String userId = "userId";
    final APIfindMyUserRequest findMyUserRequest = mock(APIfindMyUserRequest.class);
    final Get2UsersMeResponse response = mock(Get2UsersMeResponse.class);
    final User user = mock(User.class);
    final UsersApi usersApi = mock(UsersApi.class);

    when(user.getId()).thenReturn(userId);
    when(response.getData()).thenReturn(user);
    when(findMyUserRequest.execute()).thenReturn(response);
    when(usersApi.findMyUser()).thenReturn(findMyUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);

    final String result = controller.getTwitterUser();
    assertEquals(userId, result);
  }
}
