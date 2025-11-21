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

import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.pkce.PKCE
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.auth.TwitterOAuth20Service
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.io.IOException
import java.util.concurrent.ExecutionException

private const val ACCESS_TOKEN: String = "access"
private const val AUTHORIZATION_CODE: String = "code"
private const val REFRESH_TOKEN: String = "refresh"

internal class OAuth2ControllerTest {
  @Test
  fun testAuthorize() {
    val authorizationUrl = "http://localhost/authorize"
    val pkce: PKCE = mockk {}

    val twitterApiUtils: TwitterApiUtils = mockk {}

    val twitterOAuth20Service: TwitterOAuth20Service =
      mockk {
        every { getAuthorizationUrl(pkce, SECRET_STATE) } returns authorizationUrl
      }
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    val response = controller.authorize()
    Assertions.assertEquals(HttpStatus.FOUND.value(), response.statusCode.value())
    Assertions.assertEquals(
      authorizationUrl,
      response.headers.get(HttpHeaders.LOCATION)?.first(),
      "Response headers must contain redirect url",
    )
  }

  @Test
  @Throws(
    IOException::class,
    ExecutionException::class,
    InterruptedException::class,
    ApiException::class,
  )
  fun testAuthorizationCallback() {
    val code = AUTHORIZATION_CODE
    val request: HttpServletRequest =
      MockMvcRequestBuilders
        .get("/oauth2/authorize")
        .param(AUTHORIZATION_CODE_PARAMETER_NAME, code)
        .buildRequest(mockk<ServletContext>(relaxed = true))
    val oAuth2AccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns ACCESS_TOKEN
        every { refreshToken } returns REFRESH_TOKEN
      }
    val pkce: PKCE = mockk()
    val twitterApiUtils: TwitterApiUtils =
      mockk {
        every { updateAccessTokens(oAuth2AccessToken) } returns true
      }
    val twitterOAuth20Service: TwitterOAuth20Service =
      mockk {
        every { getAccessToken(pkce, code) } returns oAuth2AccessToken
      }
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    val response = controller.authorizationCallback(request)
    Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode.value())
    verify(exactly = 1) { twitterApiUtils.updateAccessTokens(oAuth2AccessToken) }
  }

  @Test
  @Throws(
    IOException::class,
    ExecutionException::class,
    InterruptedException::class,
    ApiException::class,
  )
  fun testAuthorizationCallbackUpdateFailure() {
    val code = AUTHORIZATION_CODE
    val request: HttpServletRequest =
      MockMvcRequestBuilders
        .get("/oauth2/authorize")
        .param(AUTHORIZATION_CODE_PARAMETER_NAME, code)
        .buildRequest(mockk<ServletContext>(relaxed = true))
    val oAuth2AccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns ACCESS_TOKEN
        every { refreshToken } returns REFRESH_TOKEN
      }
    val pkce: PKCE = mockk()
    val twitterApiUtils: TwitterApiUtils =
      mockk {
        every { updateAccessTokens(oAuth2AccessToken) } returns false
      }
    val twitterOAuth20Service: TwitterOAuth20Service =
      mockk {
        every { getAccessToken(pkce, code) } returns oAuth2AccessToken
      }
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    val response = controller.authorizationCallback(request)
    Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode.value())
    verify(exactly = 1) { twitterApiUtils.updateAccessTokens(oAuth2AccessToken) }
  }

  @Test
  @Throws(IOException::class, ExecutionException::class, InterruptedException::class)
  fun testAuthorizationCallbackMissingParameter() {
    val request: HttpServletRequest = MockHttpServletRequest()
    val oAuth2AccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns ACCESS_TOKEN
        every { refreshToken } returns REFRESH_TOKEN
      }
    val pkce: PKCE = mockk()
    val twitterApiUtils: TwitterApiUtils = mockk()
    val twitterOAuth20Service: TwitterOAuth20Service =
      mockk {
        every { getAccessToken(pkce, AUTHORIZATION_CODE) } returns oAuth2AccessToken
      }
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    Assertions.assertDoesNotThrow {
      val response = controller.authorizationCallback(request)
      Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode.value())
      verify(exactly = 0) { twitterApiUtils.updateAccessTokens(oAuth2AccessToken) }
    }
  }

  @Test
  @Throws(IOException::class, ExecutionException::class, InterruptedException::class)
  fun testAuthorizationCallbackFailure() {
    val code = AUTHORIZATION_CODE
    val request: HttpServletRequest =
      MockMvcRequestBuilders
        .get("/oauth2/authorize")
        .param(AUTHORIZATION_CODE_PARAMETER_NAME, code)
        .buildRequest(mockk<ServletContext>(relaxed = true))
    val pkce: PKCE = mockk()
    val twitterApiUtils: TwitterApiUtils = mockk()
    val twitterOAuth20Service: TwitterOAuth20Service =
      mockk {
        every { getAccessToken(pkce, code) } throws IOException("fail")
      }
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    Assertions.assertDoesNotThrow {
      val response = controller.authorizationCallback(request)
      Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode.value())
      verify(exactly = 0) { twitterApiUtils.updateAccessTokens(any()) }
    }
  }

  @Test
  fun testGetAccessTokens() {
    val accessToken = "accessToken"
    val refreshToken = "refreshToken"
    val pkce: PKCE = mockk()
    val twitterCredentialsOAuth2: TwitterCredentialsOAuth2 =
      mockk {
        every { twitterOauth2AccessToken } returns accessToken
        every { twitterOauth2RefreshToken } returns refreshToken
      }
    val twitterApiUtils: TwitterApiUtils =
      mockk {
        every { twitterCredentials } returns twitterCredentialsOAuth2
      }
    val twitterOAuth20Service: TwitterOAuth20Service = mockk()
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    val response = controller.getAccessTokens()
    Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode.value())
    Assertions.assertEquals(accessToken, response.getBody()?.get("accessToken"))
    Assertions.assertEquals(refreshToken, response.getBody()!!["refreshToken"])
  }

  @Test
  @Throws(ApiException::class)
  fun testRefreshTokens() {
    val mockAccessToken = "accessToken"
    val mockRefreshToken = "refreshToken"
    val pkce: PKCE = mockk()
    val oAuth2AccessToken: OAuth2AccessToken =
      mockk {
        every { accessToken } returns mockAccessToken
        every { refreshToken } returns mockRefreshToken
      }
    val mockTwitterApi: TwitterApi = mockk { every { refreshToken() } returns oAuth2AccessToken }
    val twitterApiUtils: TwitterApiUtils =
      mockk {
        every { twitterApi } returns mockTwitterApi
        every { updateAccessTokens(oAuth2AccessToken) } returns true
      }
    val twitterOAuth20Service: TwitterOAuth20Service = mockk()
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    val response = controller.refreshAccessTokens()
    Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode.value())
    Assertions.assertEquals("success", response.getBody())
  }

  @Test
  @Throws(ApiException::class)
  fun testRefreshTokensFailure() {
    val pkce: PKCE = mockk()
    val mockTwitterApi: TwitterApi = mockk { every { refreshToken() } returns null }
    val twitterApiUtils: TwitterApiUtils =
      mockk(relaxed = true) { every { twitterApi } returns mockTwitterApi }
    val twitterOAuth20Service: TwitterOAuth20Service = mockk()
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    val response = controller.refreshAccessTokens()
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode.value())
    Assertions.assertEquals("unauthorized", response.getBody())
  }

  @Test
  @Throws(ApiException::class)
  fun testRefreshTokensError() {
    val errorMessage = "test error"
    val expectedBody =
      """
      Message: test error
      HTTP response code: 0
      HTTP response body: null
      HTTP response headers: null
      """.trimIndent()
    val pkce: PKCE = mockk()
    val mockTwitterApi: TwitterApi =
      mockk {
        every { refreshToken() } throws ApiException(errorMessage)
      }
    val twitterApiUtils: TwitterApiUtils = mockk { every { twitterApi } returns mockTwitterApi }
    val twitterOAuth20Service: TwitterOAuth20Service = mockk()
    val controller = OAuth2Controller(pkce, twitterApiUtils, twitterOAuth20Service)

    val response = controller.refreshAccessTokens()
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.statusCode.value())
    Assertions.assertEquals(expectedBody, response.getBody())
  }
}
