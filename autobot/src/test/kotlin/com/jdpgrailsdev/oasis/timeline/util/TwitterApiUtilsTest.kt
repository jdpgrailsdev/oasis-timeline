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

package com.jdpgrailsdev.oasis.timeline.util

import com.github.scribejava.core.model.OAuth2AccessToken
import com.jdpgrailsdev.oasis.timeline.exception.SecurityException
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.api.TwitterApi
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private const val ACCESS_TOKEN: String = "access"
private const val REFRESH_TOKEN: String = "refresh"

/** Test suite for the [TwitterApiUtils] class. */
internal class TwitterApiUtilsTest {
  @Test
  fun testGetTwitterApi() {
    val dataStoreService: DataStoreService = mockk(relaxed = true)
    val twitterCredentials: TwitterCredentialsOAuth2 = mockk(relaxed = true)
    val twitterApiUtils = TwitterApiUtils(dataStoreService, twitterCredentials)
    val twitterApi = twitterApiUtils.getTwitterApi()
    Assertions.assertNotNull(twitterApi)
  }

  @Test
  @Throws(ApiException::class, SecurityException::class)
  fun testUpdateAccessTokens() {
    val dataStoreService: DataStoreService = mockk(relaxed = true)
    val oAuth2AccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns ACCESS_TOKEN
        every { refreshToken } returns REFRESH_TOKEN
      }
    val mockTwitterApi: TwitterApi = mockk()
    val twitterCredentials: TwitterCredentialsOAuth2 = mockk(relaxed = true)
    val twitterApiUtils: TwitterApiUtils =
      spyk(TwitterApiUtils(dataStoreService, twitterCredentials)) {
        every { getTwitterApi() } returns mockTwitterApi
      }

    Assertions.assertTrue(twitterApiUtils.updateAccessTokens(oAuth2AccessToken))
    verify(exactly = 1) { twitterCredentials.twitterOauth2AccessToken = ACCESS_TOKEN }
    verify(exactly = 1) { twitterCredentials.twitterOauth2RefreshToken = REFRESH_TOKEN }
    verify(exactly = 1) { dataStoreService.setValue(ACCESS_TOKEN_KEY, ACCESS_TOKEN) }
    verify(exactly = 1) { dataStoreService.setValue(REFRESH_TOKEN_KEY, REFRESH_TOKEN) }
  }

  @Test
  @Throws(ApiException::class, SecurityException::class)
  fun testUpdateAccessTokensBlankAccessToken() {
    val dataStoreService: DataStoreService = mockk()
    val oAuth2AccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns null
        every { refreshToken } returns REFRESH_TOKEN
      }
    val mockTwitterApi: TwitterApi = mockk()
    val twitterCredentials: TwitterCredentialsOAuth2 = mockk()

    val twitterApiUtils: TwitterApiUtils =
      spyk(TwitterApiUtils(dataStoreService, twitterCredentials)) {
        every { getTwitterApi() } returns mockTwitterApi
      }

    Assertions.assertFalse(twitterApiUtils.updateAccessTokens(oAuth2AccessToken))
    verify(exactly = 0) { twitterCredentials.twitterOauth2AccessToken = null }
    verify(exactly = 0) { twitterCredentials.twitterOauth2RefreshToken = REFRESH_TOKEN }
    verify(exactly = 0) { dataStoreService.setValue(ACCESS_TOKEN_KEY, any()) }
    verify(exactly = 0) { dataStoreService.setValue(REFRESH_TOKEN_KEY, REFRESH_TOKEN) }
  }

  @Test
  @Throws(ApiException::class, SecurityException::class)
  fun testUpdateAccessTokensBlankRefreshToken() {
    val dataStoreService: DataStoreService = mockk()
    val oAuth2AccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns ACCESS_TOKEN
        every { refreshToken } returns null
      }
    val mockTwitterApi: TwitterApi = mockk()
    val twitterCredentials: TwitterCredentialsOAuth2 = mockk()

    val twitterApiUtils: TwitterApiUtils =
      spyk(TwitterApiUtils(dataStoreService, twitterCredentials)) {
        every { getTwitterApi() } returns mockTwitterApi
      }

    Assertions.assertFalse(twitterApiUtils.updateAccessTokens(oAuth2AccessToken))
    verify(exactly = 0) { twitterCredentials.twitterOauth2AccessToken = ACCESS_TOKEN }
    verify(exactly = 0) { twitterCredentials.twitterOauth2RefreshToken = null }
    verify(exactly = 0) { dataStoreService.setValue(ACCESS_TOKEN_KEY, ACCESS_TOKEN) }
    verify(exactly = 0) { dataStoreService.setValue(REFRESH_TOKEN_KEY, any()) }
  }

  @Test
  @Throws(ApiException::class, SecurityException::class)
  fun testUpdateNullAccessTokens() {
    val dataStoreService: DataStoreService = mockk()
    val mockTwitterApi: TwitterApi = mockk()
    val twitterCredentials: TwitterCredentialsOAuth2 = mockk()

    val twitterApiUtils: TwitterApiUtils =
      spyk(TwitterApiUtils(dataStoreService, twitterCredentials)) {
        every { getTwitterApi() } returns mockTwitterApi
      }

    Assertions.assertFalse(twitterApiUtils.updateAccessTokens(null))
    verify(exactly = 0) { twitterCredentials.twitterOauth2AccessToken = ACCESS_TOKEN }
    verify(exactly = 0) { twitterCredentials.twitterOauth2RefreshToken = REFRESH_TOKEN }
    verify(exactly = 0) { dataStoreService.setValue(ACCESS_TOKEN_KEY, ACCESS_TOKEN) }
    verify(exactly = 0) { dataStoreService.setValue(REFRESH_TOKEN_KEY, REFRESH_TOKEN) }
  }
}
