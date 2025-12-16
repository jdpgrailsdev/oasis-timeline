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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.jdpgrailsdev.oasis.timeline.EndToEndIntegrationTests.AUTH_PASSWORD
import com.jdpgrailsdev.oasis.timeline.EndToEndIntegrationTests.AUTH_USERNAME
import com.jdpgrailsdev.oasis.timeline.config.IntegrationTestConfiguration
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

private val searchResultTypeReference = object : TypeReference<List<TimelineData>>() {}

@ActiveProfiles("test")
@SpringBootTest(
  classes = [IntegrationTestConfiguration::class],
  properties = ["spring.data.redis.url=redis://default:@localhost:6380"],
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@AutoConfigureMockMvc
internal class SearchIntegrationTest {
  @Autowired private lateinit var mockMvc: MockMvc

  @Autowired private lateinit var mapper: ObjectMapper

  @Test
  fun testSearch() {
    val limit = 25
    val result =
      mockMvc
        .perform(
          get("/search/timeline?query=Noel&limit=$limit")
            .with(SecurityMockMvcRequestPostProcessors.user(AUTH_USERNAME).password(AUTH_PASSWORD)),
        ).andExpect { status().isOk }
        .andReturn()

    val searchResults = readResponse(result)
    assertEquals(limit, searchResults?.size)
  }

  @Test
  fun testSearchPagination() {
    val limit = 5
    var offset = 0
    var previous: List<TimelineData>? = null

    for (i in 1..5) {
      val result =
        mockMvc
          .perform(
            get("/search/timeline?query=Noel&limit=$limit&offset=$offset")
              .with(
                SecurityMockMvcRequestPostProcessors.user(AUTH_USERNAME).password(AUTH_PASSWORD),
              ),
          ).andExpect { status().isOk }
          .andReturn()

      val searchResults = readResponse(result)
      assertEquals(limit, searchResults?.size)

      // Assert that the current results are not contained in the previous result to verify
      // pagination is working
      previous?.let { p -> assertEquals(0, searchResults.filter { p.contains(it) }.size) }

      previous = searchResults
      offset = i
    }
  }

  @Test
  fun testSearchMinScore() {
    val limit = 25
    val minScore = 2.0f
    val result =
      mockMvc
        .perform(
          get("/search/timeline?query=Noel&limit=$limit&minScore=$minScore")
            .with(SecurityMockMvcRequestPostProcessors.user(AUTH_USERNAME).password(AUTH_PASSWORD)),
        ).andExpect { status().isOk }
        .andReturn()

    val searchResults = readResponse(result)
    assertEquals(0, searchResults?.size)
  }

  @Test
  fun testSearchRequiresAuthentication() {
    mockMvc.perform(get("/search/timeline?query=Noel")).andExpect { status().isUnauthorized }
  }

  private fun readResponse(result: MvcResult) = mapper.readValue(result.response.contentAsString, searchResultTypeReference)
}
