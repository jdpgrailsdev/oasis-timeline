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

import static com.jdpgrailsdev.oasis.timeline.controller.OAuth2Controller.AUTHORIZATION_CODE_PARAMETER_NAME;
import static com.jdpgrailsdev.oasis.timeline.controller.OAuth2Controller.SECRET_STATE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SuppressWarnings({"AbbreviationAsWordInName", "PMD.CloseResource"})
class OAuth2ControllerTests {

  private static final String STATUS_CODE_FAILURE_MESSAGE = "Response status code must be equal";

  @Test
  void testAuthorize() {
    final String authorizationUrl = "http://localhost/authorize";
    final PKCE pkce = mock(PKCE.class);
    final TwitterCredentialsOAuth2 twitterCredentialsOAuth2 = mock(TwitterCredentialsOAuth2.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterCredentialsOAuth2, twitterOAuth20Service);

    when(twitterOAuth20Service.getAuthorizationUrl(pkce, SECRET_STATE))
        .thenReturn(authorizationUrl);

    final ResponseEntity<Void> response = controller.authorize();
    assertEquals(
        HttpStatus.FOUND.value(), response.getStatusCode().value(), STATUS_CODE_FAILURE_MESSAGE);
    assertEquals(
        authorizationUrl,
        response.getHeaders().get(HttpHeaders.LOCATION).get(0),
        "Response headers must contain redirect url");
  }

  @Test
  void testGetAccessToken() throws IOException, ExecutionException, InterruptedException {
    final String code = "code";
    final String accessToken = "access";
    final String refreshToken = "refresh";
    final HttpServletRequest request =
        MockMvcRequestBuilders.get("/oauth2/authorize")
            .param(AUTHORIZATION_CODE_PARAMETER_NAME, code)
            .buildRequest(mock(ServletContext.class));
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final PKCE pkce = mock(PKCE.class);
    final TwitterCredentialsOAuth2 twitterCredentialsOAuth2 = mock(TwitterCredentialsOAuth2.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterCredentialsOAuth2, twitterOAuth20Service);

    when(twitterOAuth20Service.getAccessToken(pkce, code)).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getAccessToken()).thenReturn(accessToken);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(refreshToken);

    final ResponseEntity<String> response = controller.getAccessToken(request);
    assertEquals(
        HttpStatus.OK.value(), response.getStatusCode().value(), STATUS_CODE_FAILURE_MESSAGE);
    verify(twitterCredentialsOAuth2, times(1)).setTwitterOauth2AccessToken(accessToken);
    verify(twitterCredentialsOAuth2, times(1)).setTwitterOauth2RefreshToken(refreshToken);
  }

  @Test
  void testGetAccessTokenMissingParameter()
      throws IOException, ExecutionException, InterruptedException {
    final String code = "code";
    final String accessToken = "access";
    final String refreshToken = "refresh";
    final HttpServletRequest request = new MockHttpServletRequest();
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final PKCE pkce = mock(PKCE.class);
    final TwitterCredentialsOAuth2 twitterCredentialsOAuth2 = mock(TwitterCredentialsOAuth2.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterCredentialsOAuth2, twitterOAuth20Service);

    when(twitterOAuth20Service.getAccessToken(pkce, code)).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getAccessToken()).thenReturn(accessToken);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(refreshToken);

    assertDoesNotThrow(
        () -> {
          final ResponseEntity<String> response = controller.getAccessToken(request);
          assertEquals(
              HttpStatus.BAD_REQUEST.value(),
              response.getStatusCode().value(),
              STATUS_CODE_FAILURE_MESSAGE);
          verify(twitterCredentialsOAuth2, times(0)).setTwitterOauth2AccessToken(accessToken);
          verify(twitterCredentialsOAuth2, times(0)).setTwitterOauth2RefreshToken(refreshToken);
        });
  }

  @Test
  void testGetAccessTokenFailure() throws IOException, ExecutionException, InterruptedException {
    final String code = "code";
    final HttpServletRequest request =
        MockMvcRequestBuilders.get("/oauth2/authorize")
            .param(AUTHORIZATION_CODE_PARAMETER_NAME, code)
            .buildRequest(mock(ServletContext.class));
    final PKCE pkce = mock(PKCE.class);
    final TwitterCredentialsOAuth2 twitterCredentialsOAuth2 = mock(TwitterCredentialsOAuth2.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterCredentialsOAuth2, twitterOAuth20Service);

    when(twitterOAuth20Service.getAccessToken(pkce, code)).thenThrow(new IOException("fail"));

    assertDoesNotThrow(
        () -> {
          final ResponseEntity<String> response = controller.getAccessToken(request);
          assertEquals(
              HttpStatus.UNAUTHORIZED.value(),
              response.getStatusCode().value(),
              STATUS_CODE_FAILURE_MESSAGE);
          verify(twitterCredentialsOAuth2, times(0)).setTwitterOauth2AccessToken(any());
          verify(twitterCredentialsOAuth2, times(0)).setTwitterOauth2RefreshToken(any());
        });
  }
}
