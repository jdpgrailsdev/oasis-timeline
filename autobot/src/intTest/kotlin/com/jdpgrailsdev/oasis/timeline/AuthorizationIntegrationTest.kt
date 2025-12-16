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

package com.jdpgrailsdev.oasis.timeline

import com.jdpgrailsdev.oasis.timeline.config.IntegrationTestConfiguration
import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.endpoint.EndpointsSupplier
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@SpringBootTest(
  classes = [IntegrationTestConfiguration::class],
  properties = ["spring.data.redis.url=redis://default:@localhost:6381"],
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@AutoConfigureMockMvc
internal class AuthorizationIntegrationTest {
  @Autowired private lateinit var mockMvc: MockMvc

  @Autowired private lateinit var endpointsSupplier: EndpointsSupplier<ExposableWebEndpoint>

  @Test
  fun testActuatorEndpointsRequiresAuthorization() {
    endpointsSupplier.endpoints.forEach { endpoint ->
      mockMvc.perform(get("/actuator/${endpoint.endpointId}")).andExpect { status().isUnauthorized }
    }
  }

  @Test
  fun testEventPublishingRequiresAuthorization() {
    mockMvc.perform(get("/publish/events")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testEventPublishingForTargetRequiresAuthorization() {
    mockMvc.perform(get("/publish/events/{postTarget}", PostTarget.BLUESKY)).andExpect {
      status().isUnauthorized
    }
  }

  @Test
  fun testOauth2AuthorizeRequiresAuthorization() {
    mockMvc.perform(get("/oauth2/authorize")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testOauth2CallbackRequiresAuthorization() {
    mockMvc.perform(get("/oauth2/callback")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testOauth2AccessTokensRequiresAuthorization() {
    mockMvc.perform(get("/oauth2/access_tokens")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testOauth2AccessTokensRefreshRequiresAuthorization() {
    mockMvc.perform(get("/oauth2/access_tokens/refresh")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testSearchRequiresAuthorization() {
    mockMvc.perform(get("/search/timeline?query=Noel")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testStatusCheckDoesNotRequireAuthorization() {
    mockMvc.perform(get("/status/check")).andExpect { status().isOk }
  }

  @Test
  fun testSupportEventsRequiresAuthorization() {
    mockMvc
      .perform(
        get("/support/events?date={date}&postTarget={postTarget}", "2025-12-15", PostTarget.BLUESKY),
      ).andExpect { status().isUnauthorized }
  }

  @Test
  fun testSupportRecentBlueskyPostsRequiresAuthorization() {
    mockMvc.perform(get("/support/bluesky")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testSupportRecentTwitterPostsRequiresAuthorization() {
    mockMvc.perform(get("/support/tweets")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testSupportTwitterUserRequiresAuthorization() {
    mockMvc.perform(get("/support/user")).andExpect { status().isUnauthorized }
  }

  @Test
  fun testSupportPublishTestEventRequiresAuthorization() {
    mockMvc
      .perform(get("/support/publish/events/test/{postTarget}", PostTarget.BLUESKY))
      .andExpect { status().isUnauthorized }
  }
}
