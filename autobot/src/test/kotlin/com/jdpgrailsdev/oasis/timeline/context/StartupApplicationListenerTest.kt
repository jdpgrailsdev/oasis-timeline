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

package com.jdpgrailsdev.oasis.timeline.context

import com.jdpgrailsdev.oasis.timeline.service.BlueSkyMentionCacheService
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService
import com.jdpgrailsdev.oasis.timeline.util.ACCESS_TOKEN_KEY
import com.jdpgrailsdev.oasis.timeline.util.REFRESH_TOKEN_KEY
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.TwitterCredentialsOAuth2
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.springframework.context.event.ContextRefreshedEvent
import java.io.IOException
import java.util.Optional
import com.jdpgrailsdev.oasis.timeline.exception.SecurityException as TwitterSecurityException

private const val ACCESS_TOKEN = "access"
private const val REFRESH_TOKEN = "refresh"

internal class StartupApplicationListenerTest {
  @Test
  fun testRetrieveAuthenticationCredentialsOnStartup() {
    val accessToken = ACCESS_TOKEN
    val refreshToken = REFRESH_TOKEN
    val event = mockk<ContextRefreshedEvent>()
    val blueSkyMentionCacheService =
      mockk<BlueSkyMentionCacheService> { every { loadCache() } returns Unit }
    val dataStoreService =
      mockk<DataStoreService> {
        every { getValue(ACCESS_TOKEN_KEY) } returns Optional.of(accessToken)
        every { getValue(REFRESH_TOKEN_KEY) } returns Optional.of(refreshToken)
      }
    val twitterCredentialsMock =
      mockk<TwitterCredentialsOAuth2> { every { twitterOauth2AccessToken } returns null }
    val twitterApiUtils =
      mockk<TwitterApiUtils> {
        every { getTwitterCredentials() } returns twitterCredentialsMock
        every { updateInMemoryCredentials(any(), any()) } returns Unit
      }

    val listener =
      StartupApplicationListener(
        dataStoreService = dataStoreService,
        twitterApiUtils = twitterApiUtils,
        blueSkyMentionCacheService = blueSkyMentionCacheService,
      )

    assertDoesNotThrow { listener.onApplicationEvent(event) }
    verify(exactly = 1) { twitterApiUtils.updateInMemoryCredentials(accessToken, refreshToken) }
  }

  @Test
  fun testRetrieveAuthenticationCredentialsMissingAccessTokenOnStartup() {
    val refreshToken = REFRESH_TOKEN
    val event = mockk<ContextRefreshedEvent>()
    val blueSkyMentionCacheService =
      mockk<BlueSkyMentionCacheService> { every { loadCache() } returns Unit }
    val dataStoreService =
      mockk<DataStoreService> {
        every { getValue(ACCESS_TOKEN_KEY) } returns Optional.empty()
        every { getValue(REFRESH_TOKEN_KEY) } returns Optional.of(refreshToken)
      }
    val twitterCredentialsMock =
      mockk<TwitterCredentialsOAuth2> { every { twitterOauth2AccessToken } returns null }
    val twitterApiUtils =
      mockk<TwitterApiUtils> { every { getTwitterCredentials() } returns twitterCredentialsMock }

    val listener =
      StartupApplicationListener(
        dataStoreService = dataStoreService,
        twitterApiUtils = twitterApiUtils,
        blueSkyMentionCacheService = blueSkyMentionCacheService,
      )

    assertDoesNotThrow { listener.onApplicationEvent(event) }
    verify(exactly = 0) { twitterApiUtils.updateInMemoryCredentials(ACCESS_TOKEN, refreshToken) }
  }

  @Test
  fun testRetrieveAuthenticationCredentialsMissingRefreshTokenOnStartup() {
    val accessToken = ACCESS_TOKEN
    val event = mockk<ContextRefreshedEvent>()
    val blueSkyMentionCacheService =
      mockk<BlueSkyMentionCacheService> { every { loadCache() } returns Unit }
    val dataStoreService =
      mockk<DataStoreService> {
        every { getValue(ACCESS_TOKEN_KEY) } returns Optional.of(accessToken)
        every { getValue(REFRESH_TOKEN_KEY) } returns Optional.empty()
      }
    val twitterCredentialsMock =
      mockk<TwitterCredentialsOAuth2> { every { twitterOauth2AccessToken } returns null }
    val twitterApiUtils =
      mockk<TwitterApiUtils> { every { getTwitterCredentials() } returns twitterCredentialsMock }

    val listener =
      StartupApplicationListener(
        dataStoreService = dataStoreService,
        twitterApiUtils = twitterApiUtils,
        blueSkyMentionCacheService = blueSkyMentionCacheService,
      )

    assertDoesNotThrow { listener.onApplicationEvent(event) }
    verify(exactly = 0) { twitterApiUtils.updateInMemoryCredentials(accessToken, REFRESH_TOKEN) }
  }

  @Test
  fun testRetrieveAuthenticationCredentialsDecryptionFailureOnStartup() {
    val refreshToken = REFRESH_TOKEN
    val event = mockk<ContextRefreshedEvent>()
    val blueSkyMentionCacheService =
      mockk<BlueSkyMentionCacheService> { every { loadCache() } returns Unit }
    val dataStoreService =
      mockk<DataStoreService> {
        every { getValue(ACCESS_TOKEN_KEY) } throws TwitterSecurityException("test", IOException())
        every { getValue(REFRESH_TOKEN_KEY) } returns Optional.of(refreshToken)
      }
    val twitterCredentialsMock =
      mockk<TwitterCredentialsOAuth2> { every { twitterOauth2AccessToken } returns null }
    val twitterApiUtils =
      mockk<TwitterApiUtils> { every { getTwitterCredentials() } returns twitterCredentialsMock }

    val listener =
      StartupApplicationListener(
        dataStoreService = dataStoreService,
        twitterApiUtils = twitterApiUtils,
        blueSkyMentionCacheService = blueSkyMentionCacheService,
      )

    assertDoesNotThrow { listener.onApplicationEvent(event) }
    verify(exactly = 0) { twitterApiUtils.updateInMemoryCredentials(ACCESS_TOKEN, refreshToken) }
  }

  @Test
  fun testRetrieveAuthenticationCredentialsMissingOnStartup() {
    val event = mockk<ContextRefreshedEvent>()
    val blueSkyMentionCacheService =
      mockk<BlueSkyMentionCacheService> { every { loadCache() } returns Unit }
    val dataStoreService =
      mockk<DataStoreService> {
        every { getValue(ACCESS_TOKEN_KEY) } returns Optional.empty()
        every { getValue(REFRESH_TOKEN_KEY) } returns Optional.empty()
      }
    val twitterCredentialsMock =
      mockk<TwitterCredentialsOAuth2> { every { twitterOauth2AccessToken } returns null }
    val twitterApiUtils =
      mockk<TwitterApiUtils> { every { getTwitterCredentials() } returns twitterCredentialsMock }

    val listener =
      StartupApplicationListener(
        dataStoreService = dataStoreService,
        twitterApiUtils = twitterApiUtils,
        blueSkyMentionCacheService = blueSkyMentionCacheService,
      )

    assertDoesNotThrow { listener.onApplicationEvent(event) }
    verify(exactly = 0) { twitterApiUtils.updateInMemoryCredentials(ACCESS_TOKEN, REFRESH_TOKEN) }
  }

  @Test
  fun testRetrieveAuthenticationCredentialsAlreadyInMemoryOnStartup() {
    val event = mockk<ContextRefreshedEvent>()
    val blueSkyMentionCacheService =
      mockk<BlueSkyMentionCacheService> { every { loadCache() } returns Unit }
    val dataStoreService =
      mockk<DataStoreService> {
        every { getValue(ACCESS_TOKEN_KEY) } returns Optional.empty()
        every { getValue(REFRESH_TOKEN_KEY) } returns Optional.empty()
      }
    val twitterCredentialsMock =
      mockk<TwitterCredentialsOAuth2> { every { twitterOauth2AccessToken } returns ACCESS_TOKEN }
    val twitterApiUtils =
      mockk<TwitterApiUtils> { every { getTwitterCredentials() } returns twitterCredentialsMock }

    val listener =
      StartupApplicationListener(
        dataStoreService = dataStoreService,
        twitterApiUtils = twitterApiUtils,
        blueSkyMentionCacheService = blueSkyMentionCacheService,
      )

    assertDoesNotThrow { listener.onApplicationEvent(event) }
    verify(exactly = 0) { twitterApiUtils.updateInMemoryCredentials(ACCESS_TOKEN, REFRESH_TOKEN) }
  }

  @Test
  fun testBlueSkyMentionCacheLoaded() {
    val event = mockk<ContextRefreshedEvent>()
    val blueSkyMentionCacheService =
      mockk<BlueSkyMentionCacheService> { every { loadCache() } returns Unit }
    val dataStoreService =
      mockk<DataStoreService> {
        every { getValue(ACCESS_TOKEN_KEY) } returns Optional.empty()
        every { getValue(REFRESH_TOKEN_KEY) } returns Optional.empty()
      }
    val twitterCredentialsMock =
      mockk<TwitterCredentialsOAuth2> { every { twitterOauth2AccessToken } returns null }
    val twitterApiUtils =
      mockk<TwitterApiUtils> { every { getTwitterCredentials() } returns twitterCredentialsMock }

    val listener =
      StartupApplicationListener(
        dataStoreService = dataStoreService,
        twitterApiUtils = twitterApiUtils,
        blueSkyMentionCacheService = blueSkyMentionCacheService,
      )

    assertDoesNotThrow { listener.onApplicationEvent(event) }
    verify(exactly = 1) { blueSkyMentionCacheService.loadCache() }
  }
}
