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

package com.jdpgrailsdev.oasis.timeline.util;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/** Helper class for interaction with the Twitter API and authentication. */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TwitterApiUtils {

  private static final Logger log = LoggerFactory.getLogger(TwitterApiUtils.class);

  public static final String ACCESS_TOKEN_KEY = "access-token";
  public static final String REFRESH_TOKEN_KEY = "refresh-token";

  private final Lock lock;
  private final DataStoreService dataStoreService;
  private final TwitterCredentialsOAuth2 twitterCredentials;

  public TwitterApiUtils(
      final DataStoreService dataStoreService, final TwitterCredentialsOAuth2 twitterCredentials) {
    this.lock = new ReentrantLock();
    this.dataStoreService = dataStoreService;
    this.twitterCredentials = twitterCredentials;
  }

  /**
   * Creates and returns a new {@link TwitterApi} instance built using the most recent authorization
   * credentials.
   *
   * @return A new {@link TwitterApi} instance.
   */
  public TwitterApi getTwitterApi() {
    return new TwitterApi(getTwitterCredentials());
  }

  /**
   * Returns a copy of the {@link TwitterCredentialsOAuth2} authorization credentials held by this
   * utility.
   *
   * @return A copy of the {@link TwitterCredentialsOAuth2}.
   */
  public TwitterCredentialsOAuth2 getTwitterCredentials() {
    try {
      lock.lock();
      return new TwitterCredentialsOAuth2(
          twitterCredentials.getTwitterOauth2ClientId(),
          twitterCredentials.getTwitterOAuth2ClientSecret(),
          twitterCredentials.getTwitterOauth2AccessToken(),
          twitterCredentials.getTwitterOauth2RefreshToken());
    } finally {
      lock.unlock();
    }
  }

  /**
   * Updates the access token used by {@link TwitterApi} clients created by this helper.
   *
   * @param accessToken The updated access tokens.
   * @return {@code true} if the token update is successful, {@code false} otherwise.
   * @throws ApiException if unable to validate the updated tokens.
   */
  public boolean updateAccessTokens(final OAuth2AccessToken accessToken) throws ApiException {
    try {
      if (shouldUpdateTokens(accessToken)) {
        updateInMemoryCredentials(accessToken.getAccessToken(), accessToken.getRefreshToken());
        updateStoredCredentials(accessToken.getAccessToken(), accessToken.getRefreshToken());
        log.info("Access tokens updated.");
        return true;
      } else {
        log.error("No access tokens provided.  Nothing to update.");
        return false;
      }
    } catch (final SecurityException e) {
      log.error("Unable to update retrieved credentials.", e);
      return false;
    }
  }

  public void updateInMemoryCredentials(final String accessToken, final String refreshToken) {
    try {
      lock.lock();
      twitterCredentials.setTwitterOauth2AccessToken(accessToken);
      twitterCredentials.setTwitterOauth2RefreshToken(refreshToken);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Tests whether the access token is valid and should be used to update the authentication
   * credentials.
   *
   * <p>A valid access token is one that is:
   *
   * <ul>
   *   <li>Not {@code null}
   *   <li>Has a non-blank access token
   *   <li>Has a non-blank refresh token
   * </ul>
   *
   * @param accessToken The updated access tokens.
   * @return {@code true} if the access token should be updated or {@link false} otherwise.
   */
  private boolean shouldUpdateTokens(final OAuth2AccessToken accessToken) {
    return accessToken != null
        && StringUtils.hasText(accessToken.getAccessToken())
        && StringUtils.hasText(accessToken.getRefreshToken());
  }

  /**
   * Stores the updated credentials in the datastore for later retrieval.
   *
   * @param accessToken The updated access token.
   * @param refreshToken The updated refresh token.
   * @throws SecurityException if unable to encrypt the updated tokens.
   */
  private void updateStoredCredentials(final String accessToken, final String refreshToken)
      throws SecurityException {
    dataStoreService.setValue(ACCESS_TOKEN_KEY, accessToken);
    dataStoreService.setValue(REFRESH_TOKEN_KEY, refreshToken);
  }
}
