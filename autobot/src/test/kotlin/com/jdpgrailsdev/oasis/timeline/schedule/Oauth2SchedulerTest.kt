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

package com.jdpgrailsdev.oasis.timeline.schedule

import com.github.scribejava.core.model.OAuth2AccessToken
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.api.TweetsApi
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.api.UsersApi
import com.twitter.clientlib.api.UsersApi.APIfindMyUserRequest
import com.twitter.clientlib.model.Get2UsersMeResponse
import com.twitter.clientlib.model.User
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.ImmutableTag
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Supplier

/** Test suite for the [Oauth2Scheduler] class. */
internal class Oauth2SchedulerTest {
  private lateinit var meterRegistry: MeterRegistry
  private lateinit var mockTwitterApi: TwitterApi
  private lateinit var twitterApiUtils: TwitterApiUtils

  @BeforeEach
  fun setup() {
    val timer: Timer =
      mockk {
        every { record(any<Supplier<*>>()) } answers { (firstArg() as Supplier<*>).get() }
      }
    meterRegistry =
      mockk {
        every { counter(any<String>()) } returns mockk<Counter>(relaxed = true)
        every { counter(any<String>(), any<List<Tag>>()) } returns mockk<Counter>(relaxed = true)
        every { timer(any()) } returns timer
      }
    val tweetsApi: TweetsApi = mockk()
    mockTwitterApi = mockk { every { tweets() } returns tweetsApi }
    twitterApiUtils = mockk { every { twitterApi } returns mockTwitterApi }
  }

  @Test
  @Throws(ApiException::class)
  fun testAutomaticAccessTokenRefresh() {
    val testAccessToken = "access"
    val testRefreshToken = "refresh"
    val userId = "123456"
    val oauthAccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns testAccessToken
        every { refreshToken } returns testRefreshToken
      }
    val user: User = mockk { every { id } returns userId }
    val getUsersResponse: Get2UsersMeResponse = mockk { every { data } returns user }
    val findUserRequest: APIfindMyUserRequest =
      mockk {
        every { execute() } returns getUsersResponse
      }
    val usersApi: UsersApi = mockk { every { findMyUser() } returns findUserRequest }

    every { mockTwitterApi.users() } returns usersApi
    every { mockTwitterApi.refreshToken() } returns oauthAccessToken
    every { twitterApiUtils.updateAccessTokens(oauthAccessToken) } returns true

    val scheduler = Oauth2Scheduler(meterRegistry, twitterApiUtils)

    Assertions.assertDoesNotThrow { scheduler.refreshAccessTokens() }

    verify(exactly = 1) { twitterApiUtils.updateAccessTokens(oauthAccessToken) }
    verify(exactly = 1) {
      meterRegistry.counter(
        TOKEN_REFRESH_COUNTER_NAME,
        setOf(ImmutableTag(REFRESH_RESULT_TAG_NAME, SUCCESS_RESULT)),
      )
    }
  }

  @Test
  @Throws(ApiException::class)
  fun testAutomaticAccessTokenRefreshNull() {
    val userId = "123456"
    val user: User = mockk { every { id } returns userId }
    val getUsersResponse: Get2UsersMeResponse = mockk { every { data } returns user }
    val findUserRequest: APIfindMyUserRequest =
      mockk {
        every { execute() } returns getUsersResponse
      }
    val usersApi: UsersApi = mockk { every { findMyUser() } returns findUserRequest }

    every { mockTwitterApi.users() } returns usersApi
    every { mockTwitterApi.refreshToken() } returns null
    every { twitterApiUtils.updateAccessTokens(null) } returns false
    val scheduler = Oauth2Scheduler(meterRegistry, twitterApiUtils)

    Assertions.assertDoesNotThrow { scheduler.refreshAccessTokens() }
    verify(exactly = 1) { twitterApiUtils.updateAccessTokens(null) }
    verify(exactly = 1) {
      meterRegistry.counter(
        TOKEN_REFRESH_COUNTER_NAME,
        setOf(ImmutableTag(REFRESH_RESULT_TAG_NAME, FAILURE_RESULT)),
      )
    }
  }

  @Test
  @Throws(ApiException::class)
  fun testAutomaticAccessTokenRefreshFailure() {
    val testAccessToken = "access"
    val testRefreshToken = "refresh"
    val userId = "123456"
    val oauthAccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns testAccessToken
        every { refreshToken } returns testRefreshToken
      }
    val user: User = mockk { every { id } returns userId }
    val getUsersResponse: Get2UsersMeResponse = mockk { every { data } returns user }
    val findUserRequest: APIfindMyUserRequest =
      mockk {
        every { execute() } returns getUsersResponse
      }
    val usersApi: UsersApi = mockk { every { findMyUser() } returns findUserRequest }

    every { mockTwitterApi.users() } returns usersApi
    every { mockTwitterApi.refreshToken() } throws ApiException("test")
    val scheduler = Oauth2Scheduler(meterRegistry, twitterApiUtils)

    Assertions.assertDoesNotThrow { scheduler.refreshAccessTokens() }
    verify(exactly = 0) { twitterApiUtils.updateAccessTokens(oauthAccessToken) }
    verify(exactly = 1) {
      meterRegistry.counter(
        TOKEN_REFRESH_COUNTER_NAME,
        setOf(ImmutableTag(REFRESH_RESULT_TAG_NAME, FAILURE_RESULT)),
      )
    }
  }

  @Test
  @Throws(ApiException::class)
  fun testAutomaticAccessTokenRefreshValidationFailure() {
    val testAccessToken = "access"
    val testRefreshToken = "refresh"
    val oauthAccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns testAccessToken
        every { refreshToken } returns testRefreshToken
      }

    every { mockTwitterApi.refreshToken() } returns oauthAccessToken
    every { twitterApiUtils.updateAccessTokens(oauthAccessToken) } throws ApiException()

    val scheduler = Oauth2Scheduler(meterRegistry, twitterApiUtils)

    Assertions.assertDoesNotThrow { scheduler.refreshAccessTokens() }

    verify(exactly = 1) { twitterApiUtils.updateAccessTokens(oauthAccessToken) }
    verify(exactly = 1) {
      meterRegistry.counter(
        TOKEN_REFRESH_COUNTER_NAME,
        setOf(ImmutableTag(REFRESH_RESULT_TAG_NAME, "failure")),
      )
    }
  }
}
