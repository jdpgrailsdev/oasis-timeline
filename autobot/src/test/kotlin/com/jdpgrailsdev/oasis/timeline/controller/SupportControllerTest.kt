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

import com.github.javafaker.Faker
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.data.Post
import com.jdpgrailsdev.oasis.timeline.data.PostException
import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import com.jdpgrailsdev.oasis.timeline.service.PostPublisherService
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.api.TweetsApi
import com.twitter.clientlib.api.TweetsApi.APItweetsRecentSearchRequest
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.api.UsersApi
import com.twitter.clientlib.api.UsersApi.APIfindMyUserRequest
import com.twitter.clientlib.model.Get2TweetsSearchRecentResponse
import com.twitter.clientlib.model.Get2UsersMeResponse
import com.twitter.clientlib.model.Tweet
import com.twitter.clientlib.model.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class SupportControllerTest {
  private lateinit var blueSkyClient: BlueSkyClient
  private lateinit var postFormatUtils: PostFormatUtils
  private lateinit var publisherService: PostPublisherService<*>
  private lateinit var timelineData: TimelineData
  private lateinit var timelineDataLoader: TimelineDataLoader
  private lateinit var mockTwitterApi: TwitterApi
  private lateinit var controller: SupportController

  @BeforeEach
  @Throws(PostException::class)
  fun setup() {
    blueSkyClient = mockk()
    val faker = Faker()
    val post: Post = mockk()
    postFormatUtils =
      mockk {
        every { generatePost(any<TimelineData>(), any(), any<PostTarget>()) } returns post
      }
    publisherService = mockk(relaxed = true)
    timelineData = mockk()
    timelineDataLoader =
      mockk {
        every { getHistory(any()) } returns listOf(timelineData, timelineData)
        every { getAdditionalHistoryContext(any()) } returns emptyList()
      }
    mockTwitterApi = mockk()
    val twitterApiUtils: TwitterApiUtils = mockk { every { twitterApi } returns mockTwitterApi }
    val publishers = listOf(publisherService)

    controller =
      SupportController(
        blueSkyClient,
        DateUtils(),
        faker,
        postFormatUtils,
        publishers,
        timelineDataLoader,
        twitterApiUtils,
      )
  }

  @ParameterizedTest
  @EnumSource(PostTarget::class)
  @DisplayName("test that when a request is made, all matching events are returned")
  fun testValidRequest(postTarget: PostTarget) {
    val date = "2020-08-04"
    val response = controller.getEvents(date, postTarget)
    Assertions.assertEquals(2, response.size)
  }

  @ParameterizedTest
  @EnumSource(PostTarget::class)
  @DisplayName(
    (
      "test that when a request is made but the controller is unable to generate the tweet text," +
        " the events are left out of the response"
    ),
  )
  @Throws(PostException::class)
  fun testInvalidRequest(postTarget: PostTarget) {
    every { timelineDataLoader.getHistory(any()) } returns listOf(timelineData, timelineData)
    every { postFormatUtils.generatePost(any<TimelineData>(), any(), any<PostTarget>()) } throws
      PostException("test")

    val date = "2020-08-04"
    val response = controller.getEvents(date, postTarget)
    Assertions.assertEquals(0, response.size)
  }

  @Test
  fun testGetRecentBlueSkyPosts() {
    val postText = "Hello world!"
    every { blueSkyClient.getPosts() } returns listOf(postText)

    val recentPosts = controller.getRecentBlueSkyPosts()
    Assertions.assertEquals(1, recentPosts.size)
    Assertions.assertEquals(postText, recentPosts.first())
  }

  @Test
  @Throws(ApiException::class)
  fun testGetRecentTweets() {
    val tweetText = "Hello world!"
    val tweet: Tweet = mockk { every { text } returns tweetText }
    val get2TweetsSearchRecentResponse: Get2TweetsSearchRecentResponse =
      mockk {
        every { data } returns listOf(tweet)
      }
    val apiTweetsRecentSearchRequest: APItweetsRecentSearchRequest =
      mockk {
        every { execute() } returns get2TweetsSearchRecentResponse
      }
    val tweetsApi: TweetsApi =
      mockk {
        every { tweetsRecentSearch(any()) } returns apiTweetsRecentSearchRequest
      }
    every { mockTwitterApi.tweets() } returns tweetsApi

    val recentTweets = controller.getRecentTweets()
    Assertions.assertEquals(1, recentTweets.size)
    Assertions.assertEquals(tweetText, recentTweets.first())
  }

  @Test
  @Throws(ApiException::class)
  fun testGetUser() {
    val userId = "userId"
    val user: User = mockk { every { id } returns userId }
    val response: Get2UsersMeResponse = mockk { every { data } returns user }
    val findMyUserRequest: APIfindMyUserRequest = mockk { every { execute() } returns response }
    val usersApi: UsersApi = mockk { every { findMyUser() } returns findMyUserRequest }
    every { mockTwitterApi.users() } returns usersApi

    val result = controller.getTwitterUser()
    Assertions.assertEquals(userId, result)
  }

  @Test
  @DisplayName("test that when a test event is published, the underlying publish is invoked")
  @Throws(PostException::class)
  fun testPublishingTestEvent() {
    val post: Post = mockk()
    val postTarget = PostTarget.BLUESKY

    every { publisherService.getPostTarget() } returns postTarget
    every {
      postFormatUtils.generatePost(
        any<String>(),
        any<TimelineDataType>(),
        any<Int>(),
        any<PostTarget>(),
        any(),
      )
    } returns post

    controller.publishTestEventsToSocialNetwork(postTarget, null)

    verify(exactly = 1) { publisherService.publish(post) }
  }

  @Test
  @DisplayName(
    (
      "test that when a test event is published with a specific timeline data type, the underlying" +
        " publish is invoked"
    ),
  )
  @Throws(PostException::class)
  fun testPublishingTestEventWithSpecificTimelineDataType() {
    val post: Post = mockk()
    val postTarget = PostTarget.BLUESKY

    every { publisherService.getPostTarget() } returns postTarget
    every {
      postFormatUtils.generatePost(
        any<String>(),
        any<TimelineDataType>(),
        any<Int>(),
        any<PostTarget>(),
        any(),
      )
    } returns post

    controller.publishTestEventsToSocialNetwork(postTarget, TimelineDataType.VIDEOS)

    verify(exactly = 1) { publisherService.publish(post) }
  }
}
