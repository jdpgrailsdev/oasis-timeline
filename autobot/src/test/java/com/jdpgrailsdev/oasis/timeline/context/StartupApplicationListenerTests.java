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

package com.jdpgrailsdev.oasis.timeline.context;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.context.event.ContextRefreshedEvent;

/** Test suite for the {@link StartupApplicationListener} class. */
class StartupApplicationListenerTests {

  private static final String ACCESS_TOKEN = "access";
  private static final String REFRESH_TOKEN = "refresh";

  @Test
  void testRetrieveAuthenticationCredentialsOnStartup() throws SecurityException {
    final String accessToken = ACCESS_TOKEN;
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(dataStoreService.getValue(TwitterApiUtils.ACCESS_TOKEN_KEY))
        .thenReturn(Optional.of(accessToken));
    when(dataStoreService.getValue(TwitterApiUtils.REFRESH_TOKEN_KEY))
        .thenReturn(Optional.of(refreshToken));
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(1)).updateInMemoryCredentials(accessToken, refreshToken);
  }

  @Test
  void testRetrieveAuthenticationCredentialsMissingAccessTokenOnStartup() throws SecurityException {
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(dataStoreService.getValue(TwitterApiUtils.REFRESH_TOKEN_KEY))
        .thenReturn(Optional.of(refreshToken));
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(ACCESS_TOKEN, refreshToken);
  }

  @Test
  void testRetrieveAuthenticationCredentialsMissingRefreshTokenOnStartup()
      throws SecurityException {
    final String accessToken = ACCESS_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(dataStoreService.getValue(TwitterApiUtils.ACCESS_TOKEN_KEY))
        .thenReturn(Optional.of(accessToken));
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(accessToken, REFRESH_TOKEN);
  }

  @Test
  void testRetrieveAuthenticationCredentialsDecryptionFailureOnStartup() throws SecurityException {
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(dataStoreService.getValue(TwitterApiUtils.ACCESS_TOKEN_KEY))
        .thenThrow(SecurityException.class);
    when(dataStoreService.getValue(TwitterApiUtils.REFRESH_TOKEN_KEY))
        .thenReturn(Optional.of(refreshToken));
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(ACCESS_TOKEN, refreshToken);
  }

  @Test
  void testRetrieveAuthenticationCredentialsMissingOnStartup() {
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(ACCESS_TOKEN, REFRESH_TOKEN);
  }

  @Test
  void testRetrieveAuthenticationCredentialsAlreadyInMemoryOnStartup() {
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(twitterCredentials.getTwitterOauth2AccessToken()).thenReturn(ACCESS_TOKEN);
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(ACCESS_TOKEN, REFRESH_TOKEN);
  }
}
