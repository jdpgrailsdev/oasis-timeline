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

package com.jdpgrailsdev.oasis.timeline.controller

import com.github.scribejava.core.pkce.PKCE
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.auth.TwitterOAuth20Service
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.IOException
import java.net.URI
import java.util.concurrent.ExecutionException

private val logger = KotlinLogging.logger {}

internal const val AUTHORIZATION_CODE_PARAMETER_NAME = "code"
internal const val SECRET_STATE = "state"

/** This [Controller] provides endpoints to assist in authorization via OAuth2. */
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Controller
@RequestMapping("/oauth2")
class OAuth2Controller(
  private val pkce: PKCE,
  private val twitterApiUtils: TwitterApiUtils,
  private val twitterOAuth2Service: TwitterOAuth20Service,
) {
  /**
   * Generates the OAuth2 authorization URL and redirects the caller to it.
   *
   * @return A redirect to the OAuth2 authorization URL.
   */
  @GetMapping("authorize")
  fun authorize(): ResponseEntity<Unit> {
    val authorizationUrl = twitterOAuth2Service.getAuthorizationUrl(pkce, SECRET_STATE)
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(authorizationUrl)).build()
  }

  /**
   * Callback endpoint that handles the retrieve of the OAuth2 access token from the authorization
   * code.
   *
   * @param request An [HttpServletRequest] that includes the OAuth2 authorization code.
   * @return An empty response with either a [HttpStatus.OK] status code if the access token has
   *   been successfully retrieved or [HttpStatus.UNAUTHORIZED] if unable to retrieve the access
   *   code.
   */
  @GetMapping("callback")
  fun authorizationCallback(request: HttpServletRequest): ResponseEntity<String> {
    try {
      val authorizationCode = request.getParameter(AUTHORIZATION_CODE_PARAMETER_NAME)
      if (StringUtils.hasText(authorizationCode)) {
        logger.info { "Generating access token from authorization code..." }
        val accessToken = twitterOAuth2Service.getAccessToken(pkce, authorizationCode)
        if (twitterApiUtils.updateAccessTokens(accessToken)) {
          logger.info { "Access token successfully generated from authorization code." }
          return ResponseEntity.status(HttpStatus.OK).body("OK")
        } else {
          val failureMessage = "Unable to update access tokens."
          logger.error { failureMessage }
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureMessage)
        }
      } else {
        logger.error {
          "Request does not contain parameter $AUTHORIZATION_CODE_PARAMETER_NAME containing the authorization code."
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing authorization code.")
      }
    } catch (e: ApiException) {
      val errorMessage = "Unable to retrieve access token."
      logger.error(e) { errorMessage }
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage)
    } catch (e: IOException) {
      val errorMessage = "Unable to retrieve access token."
      logger.error(e) { errorMessage }
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage)
    } catch (e: ExecutionException) {
      val errorMessage = "Unable to retrieve access token."
      logger.error(e) { errorMessage }
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage)
    } catch (e: InterruptedException) {
      val errorMessage = "Unable to retrieve access token."
      logger.error(e) { errorMessage }
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage)
    }
  }

  /**
   * Returns the currently held access tokens.
   *
   * @return The currently held access tokens.
   */
  @GetMapping("access_tokens")
  fun getAccessTokens(): ResponseEntity<Map<String, String>> =
    ResponseEntity
      .status(HttpStatus.OK)
      .body(
        mapOf(
          "accessToken" to twitterApiUtils.twitterCredentials.twitterOauth2AccessToken,
          "refreshToken" to twitterApiUtils.twitterCredentials.twitterOauth2RefreshToken,
        ),
      )

  /**
   * Performs a refresh of the access tokens.
   *
   * @return The result of the access token refresh.
   */
  @GetMapping("access_tokens/refresh")
  fun refreshAccessTokens(): ResponseEntity<String> {
    try {
      val accessToken = twitterApiUtils.twitterApi.refreshToken()
      logger.info { "Successfully refreshed access tokens." }
      if (twitterApiUtils.updateAccessTokens(accessToken)) {
        logger.info { "Successfully updated access tokens." }
        return ResponseEntity.status(HttpStatus.OK).body("success")
      } else {
        logger.error { "Unable to retrieve access tokens." }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized")
      }
    } catch (e: ApiException) {
      logger.error(e) { "Unable to refresh access tokens." }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }
  }
}
