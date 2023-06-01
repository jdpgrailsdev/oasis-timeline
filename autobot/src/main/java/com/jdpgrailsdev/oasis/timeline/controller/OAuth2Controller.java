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

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** This {@link Controller} provides endpoints to assist in authorization via OAuth2. */
@SuppressWarnings("AbbreviationAsWordInName")
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

  private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);
  public static final String AUTHORIZATION_CODE_PARAMETER_NAME = "code";
  public static final String SECRET_STATE = "state";

  private final PKCE pkce;
  private final TwitterCredentialsOAuth2 twitterCredentials;
  private final TwitterOAuth20Service twitterOAuth2Service;

  public OAuth2Controller(
      final PKCE pkce,
      final TwitterCredentialsOAuth2 twitterCredentials,
      final TwitterOAuth20Service twitterOAuth2Service) {
    this.pkce = pkce;
    this.twitterCredentials = twitterCredentials;
    this.twitterOAuth2Service = twitterOAuth2Service;
  }

  /**
   * Generates the OAuth2 authorization URL and redirects the caller to it.
   *
   * @return A redirect to the OAuth2 authorization URL.
   */
  @GetMapping("authorize")
  public ResponseEntity<Void> authorize() {
    final String authorizationUrl = twitterOAuth2Service.getAuthorizationUrl(pkce, SECRET_STATE);
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(authorizationUrl)).build();
  }

  /**
   * Callback endpoint that handles the retrieve of the OAuth2 access token from the authorization
   * code.
   *
   * @param request An {@link HttpServletRequest} that includes the OAuth2 authorization code.
   * @return An empty response with either a {@link HttpStatus#OK} status code if the access token
   *     has been successfully retrieved or {@link HttpStatus#UNAUTHORIZED} if unable to retrieve
   *     the access code.
   */
  @GetMapping("callback")
  public ResponseEntity<Void> getAccessToken(final HttpServletRequest request) {
    try {
      final String authorizationCode = request.getParameter(AUTHORIZATION_CODE_PARAMETER_NAME);
      if (StringUtils.hasText(authorizationCode)) {
        final OAuth2AccessToken accessToken =
            twitterOAuth2Service.getAccessToken(pkce, authorizationCode);
        twitterCredentials.setTwitterOauth2AccessToken(accessToken.getAccessToken());
        twitterCredentials.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).build();
      } else {
        log.error(
            "Request does not contain parameter {} containing the authorization code.",
            AUTHORIZATION_CODE_PARAMETER_NAME);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    } catch (final IOException | ExecutionException | InterruptedException e) {
      log.error("Unable to retrieve access token.", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }
}
