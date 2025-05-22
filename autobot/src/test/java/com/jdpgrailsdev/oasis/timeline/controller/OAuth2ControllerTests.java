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
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SuppressWarnings({"AbbreviationAsWordInName", "PMD.CloseResource"})
class OAuth2ControllerTests {

  private static final String ACCESS_TOKEN = "access";
  private static final String AUTHORIZATION_CODE = "code";
  private static final String REFRESH_TOKEN = "refresh";

  @Test
  void testAuthorize() {
    final String authorizationUrl = "http://localhost/authorize";
    final PKCE pkce = mock(PKCE.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(twitterOAuth20Service.getAuthorizationUrl(pkce, SECRET_STATE))
        .thenReturn(authorizationUrl);

    final ResponseEntity<Void> response = controller.authorize();
    assertEquals(HttpStatus.FOUND.value(), response.getStatusCode().value());
    assertEquals(
        authorizationUrl,
        Objects.requireNonNull(response.getHeaders().get(HttpHeaders.LOCATION)).getFirst(),
        "Response headers must contain redirect url");
  }

  @Test
  void testAuthorizationCallback()
      throws IOException, ExecutionException, InterruptedException, ApiException {
    final String code = AUTHORIZATION_CODE;
    final HttpServletRequest request =
        MockMvcRequestBuilders.get("/oauth2/authorize")
            .param(AUTHORIZATION_CODE_PARAMETER_NAME, code)
            .buildRequest(mock(ServletContext.class));
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final PKCE pkce = mock(PKCE.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(twitterOAuth20Service.getAccessToken(pkce, code)).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getAccessToken()).thenReturn(ACCESS_TOKEN);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(REFRESH_TOKEN);
    when(twitterApiUtils.updateAccessTokens(oAuth2AccessToken)).thenReturn(true);

    final ResponseEntity<String> response = controller.authorizationCallback(request);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    verify(twitterApiUtils, times(1)).updateAccessTokens(oAuth2AccessToken);
  }

  @Test
  void testAuthorizationCallbackUpdateFailure()
      throws IOException, ExecutionException, InterruptedException, ApiException {
    final String code = AUTHORIZATION_CODE;
    final HttpServletRequest request =
        MockMvcRequestBuilders.get("/oauth2/authorize")
            .param(AUTHORIZATION_CODE_PARAMETER_NAME, code)
            .buildRequest(mock(ServletContext.class));
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final PKCE pkce = mock(PKCE.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(twitterOAuth20Service.getAccessToken(pkce, code)).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getAccessToken()).thenReturn(ACCESS_TOKEN);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(REFRESH_TOKEN);
    when(twitterApiUtils.updateAccessTokens(oAuth2AccessToken)).thenReturn(false);

    final ResponseEntity<String> response = controller.authorizationCallback(request);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    verify(twitterApiUtils, times(1)).updateAccessTokens(oAuth2AccessToken);
  }

  @Test
  void testAuthorizationCallbackMissingParameter()
      throws IOException, ExecutionException, InterruptedException {
    final HttpServletRequest request = new MockHttpServletRequest();
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final PKCE pkce = mock(PKCE.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(twitterOAuth20Service.getAccessToken(pkce, AUTHORIZATION_CODE))
        .thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getAccessToken()).thenReturn(ACCESS_TOKEN);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(REFRESH_TOKEN);

    assertDoesNotThrow(
        () -> {
          final ResponseEntity<String> response = controller.authorizationCallback(request);
          assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
          verify(twitterApiUtils, times(0)).updateAccessTokens(oAuth2AccessToken);
        });
  }

  @Test
  void testAuthorizationCallbackFailure()
      throws IOException, ExecutionException, InterruptedException {
    final String code = AUTHORIZATION_CODE;
    final HttpServletRequest request =
        MockMvcRequestBuilders.get("/oauth2/authorize")
            .param(AUTHORIZATION_CODE_PARAMETER_NAME, code)
            .buildRequest(mock(ServletContext.class));
    final PKCE pkce = mock(PKCE.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(twitterOAuth20Service.getAccessToken(pkce, code)).thenThrow(new IOException("fail"));

    assertDoesNotThrow(
        () -> {
          final ResponseEntity<String> response = controller.authorizationCallback(request);
          assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
          verify(twitterApiUtils, times(0)).updateAccessTokens(any());
        });
  }

  @Test
  void testGetAccessTokens() {
    final String accessToken = "accessToken";
    final String refreshToken = "refreshToken";
    final PKCE pkce = mock(PKCE.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(twitterCredentials.getTwitterOauth2AccessToken()).thenReturn(accessToken);
    when(twitterCredentials.getTwitterOauth2RefreshToken()).thenReturn(refreshToken);
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final ResponseEntity<Map<String, String>> response = controller.getAccessTokens();
    assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    assertEquals(accessToken, Objects.requireNonNull(response.getBody()).get("accessToken"));
    assertEquals(refreshToken, response.getBody().get("refreshToken"));
  }

  @Test
  void testRefreshTokens() throws ApiException {
    final String accessToken = "accessToken";
    final String refreshToken = "refreshToken";
    final PKCE pkce = mock(PKCE.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(accessToken);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(refreshToken);
    when(twitterApi.refreshToken()).thenReturn(oAuth2AccessToken);
    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);
    when(twitterApiUtils.updateAccessTokens(oAuth2AccessToken)).thenReturn(true);

    final ResponseEntity<String> response = controller.refreshAccessTokens();
    assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    assertEquals("success", response.getBody());
  }

  @Test
  void testRefreshTokensFailure() throws ApiException {
    final PKCE pkce = mock(PKCE.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(twitterApi.refreshToken()).thenReturn(null);
    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    final ResponseEntity<String> response = controller.refreshAccessTokens();
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    assertEquals("unauthorized", response.getBody());
  }

  @Test
  void testRefreshTokensError() throws ApiException {
    final String errorMessage = "test error";
    final String expectedBody =
        """
                Message: test error
                HTTP response code: 0
                HTTP response body: null
                HTTP response headers: null""";
    final PKCE pkce = mock(PKCE.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterOAuth20Service twitterOAuth20Service = mock(TwitterOAuth20Service.class);
    final OAuth2Controller controller =
        new OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service);

    when(twitterApi.refreshToken()).thenThrow(new ApiException(errorMessage));
    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    final ResponseEntity<String> response = controller.refreshAccessTokens();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
    assertEquals(expectedBody, response.getBody());
  }
}
