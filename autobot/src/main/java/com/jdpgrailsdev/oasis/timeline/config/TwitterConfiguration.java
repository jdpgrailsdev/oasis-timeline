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

package com.jdpgrailsdev.oasis.timeline.config;

import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring configuration for Twitter related beans. */
@Configuration
public class TwitterConfiguration {

  @Bean
  public TwitterCredentialsOAuth2 twitterCredentials(
      @Value("${TWITTER_OAUTH2_CLIENT_ID}") final String clientId,
      @Value("${TWITTER_OAUTH2_CLIENT_SECRET}") final String clientSecret,
      @Value("${TWITTER_OAUTH2_ACCESS_TOKEN:}") final String accessToken,
      @Value("${TWITTER_OAUTH2_REFRESH_TOKEN:}") final String refreshToken) {
    return new TwitterCredentialsOAuth2(clientId, clientSecret, accessToken, refreshToken);
  }

  @Bean
  public TwitterApiUtils twitterApiUtils(
      final DataStoreService dataStoreService, final TwitterCredentialsOAuth2 twitterCredentials) {
    return new TwitterApiUtils(dataStoreService, twitterCredentials);
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  @Bean
  public TwitterOAuth20Service twitterOAuth2Service(
      @Value("${server.base-url}") final String baseUrl,
      @Value("${oauth2.twitter.scopes}") final String scopes,
      final TwitterCredentialsOAuth2 twitterCredentials) {
    return new TwitterOAuth20Service(
        twitterCredentials.getTwitterOauth2ClientId(),
        twitterCredentials.getTwitterOAuth2ClientSecret(),
        String.format("%s/oauth2/callback", baseUrl),
        scopes);
  }

  @Bean
  public PKCE pkce(@Value("${oauth2.pkce.challenge}") final String challenge) {
    final PKCE pkce = new PKCE();
    pkce.setCodeChallenge(challenge);
    pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
    pkce.setCodeVerifier(challenge);
    return pkce;
  }
}
