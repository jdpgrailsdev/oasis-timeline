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

package com.jdpgrailsdev.oasis.timeline.config

import com.github.scribejava.core.pkce.PKCE
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.auth.TwitterOAuth20Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Spring configuration for Twitter related beans. */
@Suppress("UNUSED")
@Configuration
class TwitterConfiguration {
  @Bean
  fun twitterCredentials(
    @Value($$"${TWITTER_OAUTH2_CLIENT_ID}") clientId: String?,
    @Value($$"${TWITTER_OAUTH2_CLIENT_SECRET}") clientSecret: String?,
    @Value($$"${TWITTER_OAUTH2_ACCESS_TOKEN:}") accessToken: String?,
    @Value($$"${TWITTER_OAUTH2_REFRESH_TOKEN:}") refreshToken: String?,
  ): TwitterCredentialsOAuth2 = TwitterCredentialsOAuth2(clientId, clientSecret, accessToken, refreshToken)

  @Bean
  fun twitterApiUtils(
    dataStoreService: DataStoreService,
    twitterCredentials: TwitterCredentialsOAuth2,
  ): TwitterApiUtils = TwitterApiUtils(dataStoreService, twitterCredentials)

  @Bean
  fun twitterOAuth2Service(
    @Value($$"${server.base-url}") baseUrl: String?,
    @Value($$"${oauth2.twitter.scopes}") scopes: String?,
    twitterCredentials: TwitterCredentialsOAuth2,
  ): TwitterOAuth20Service =
    TwitterOAuth20Service(
      twitterCredentials.twitterOauth2ClientId,
      twitterCredentials.twitterOAuth2ClientSecret,
      "$baseUrl/oauth2/callback",
      scopes,
    )

  @Bean
  fun pkce(
    @Value($$"${oauth2.pkce.challenge}") challenge: String?,
  ): PKCE {
    val pkce = PKCE()
    pkce.codeChallenge = challenge
    pkce.codeChallengeMethod = PKCECodeChallengeMethod.PLAIN
    pkce.codeVerifier = challenge
    return pkce
  }
}
