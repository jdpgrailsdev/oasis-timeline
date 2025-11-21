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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.locks.ReentrantLock

private val logger = KotlinLogging.logger {}

const val ACCESS_TOKEN_KEY = "access-token"
const val REFRESH_TOKEN_KEY = "refresh-token"

/** Helper class for interaction with the Twitter API and authentication. */
@SuppressFBWarnings("EI_EXPOSE_REP", "EI_EXPOSE_REP2")
class TwitterApiUtils(
  private val dataStoreService: DataStoreService,
  private val twitterCredentials: TwitterCredentialsOAuth2,
) {
  private val lock = ReentrantLock()

  /**
   * Creates and returns a new [TwitterApi] instance built using the most recent authorization
   * credentials.
   *
   * @return A new [TwitterApi] instance.
   */
  fun getTwitterApi(): TwitterApi = TwitterApi(getTwitterCredentials())

  /**
   * Returns a copy of the [TwitterCredentialsOAuth2] authorization credentials held by this
   * utility.
   *
   * @return A copy of the [TwitterCredentialsOAuth2].
   */
  fun getTwitterCredentials() =
    try {
      lock.lock()
      TwitterCredentialsOAuth2(
        twitterCredentials.twitterOauth2ClientId,
        twitterCredentials.twitterOAuth2ClientSecret,
        twitterCredentials.twitterOauth2AccessToken,
        twitterCredentials.twitterOauth2RefreshToken,
      )
    } finally {
      lock.unlock()
    }

  /**
   * Updates the access token used by [TwitterApi] clients created by this helper.
   *
   * @param accessToken The updated access tokens.
   * @return `true` if the token update is successful, `false` otherwise.
   * @throws ApiException if unable to validate the updated tokens.
   */
  @Throws(ApiException::class)
  fun updateAccessTokens(accessToken: OAuth2AccessToken?) =
    try {
      if (shouldUpdateTokens(accessToken)) {
        updateInMemoryCredentials(accessToken!!.accessToken, accessToken.refreshToken)
        updateStoredCredentials(accessToken.accessToken, accessToken.refreshToken)
        logger.info { "Access tokens updated." }
        true
      } else {
        logger.error { "No access tokens provided.  Nothing to update." }
        false
      }
    } catch (e: SecurityException) {
      logger.error(e) { "Unable to update retrieved credentials." }
      false
    }

  fun updateInMemoryCredentials(
    accessToken: String,
    refreshToken: String,
  ) {
    try {
      lock.lock()
      twitterCredentials.twitterOauth2AccessToken = accessToken
      twitterCredentials.twitterOauth2RefreshToken = refreshToken
    } finally {
      lock.unlock()
    }
  }

  /**
   * Tests whether the access token is valid and should be used to update the authentication
   * credentials.
   *
   * A valid access token is one that is:
   * * Not `null`
   * * Has a non-blank access token
   * * Has a non-blank refresh token
   *
   * @param accessToken The updated access tokens.
   * @return `true` if the access token should be updated or `false` otherwise.
   */
  private fun shouldUpdateTokens(accessToken: OAuth2AccessToken?) =
    accessToken?.let { token ->
      !token.accessToken.isNullOrBlank() && !token.refreshToken.isNullOrBlank()
    } ?: false

  /**
   * Stores the updated credentials in the datastore for later retrieval.
   *
   * @param accessToken The updated access token.
   * @param refreshToken The updated refresh token.
   * @throws SecurityException if unable to encrypt the updated tokens.
   */
  @Throws(SecurityException::class)
  private fun updateStoredCredentials(
    accessToken: String,
    refreshToken: String,
  ) {
    dataStoreService.setValue(ACCESS_TOKEN_KEY, accessToken)
    dataStoreService.setValue(REFRESH_TOKEN_KEY, refreshToken)
  }
}
