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

package com.jdpgrailsdev.oasis.timeline.service

import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyProfileResponse
import com.jdpgrailsdev.oasis.timeline.config.BlueSkyContext
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException

internal class BlueSkyMentionCacheServiceTest {
  @Test
  fun testLoadingCacheOnInitialization() {
    val did = "did:plc:2kfn5kwq4dqzncgv2g2tqmii"
    val mention = "test.bsky.social"
    val profileResponse = BlueSkyProfileResponse(did = did, handle = mention)
    val blueSkyClient = mockk<BlueSkyClient> { every { getProfile(any()) } returns profileResponse }
    val blueSkyContext =
      mockk<BlueSkyContext> { every { getMentions() } returns mapOf("Test" to mention) }

    val blueSkyMentionCacheService =
      BlueSkyMentionCacheService(blueSkyClient = blueSkyClient, blueSkyContext = blueSkyContext)

    blueSkyMentionCacheService.afterPropertiesSet()
    blueSkyMentionCacheService.loadCache()

    assertEquals(did, blueSkyMentionCacheService.resolveDidForMention(mention = mention))
  }

  @Test
  fun testLoadingCacheWithException() {
    val mention = "test.bsky.social"
    val blueSkyClient =
      mockk<BlueSkyClient> { every { getProfile(any()) } throws IOException("test") }
    val blueSkyContext =
      mockk<BlueSkyContext> { every { getMentions() } returns mapOf("Test" to mention) }

    val blueSkyMentionCacheService =
      BlueSkyMentionCacheService(blueSkyClient = blueSkyClient, blueSkyContext = blueSkyContext)

    blueSkyMentionCacheService.afterPropertiesSet()

    assertDoesNotThrow { blueSkyMentionCacheService.loadCache() }
  }

  @Test
  fun testResolveDidUnresolved() {
    val mention = "test.bsky.social"
    val blueSkyClient =
      mockk<BlueSkyClient> { every { getProfile(any()) } throws IOException("test") }
    val blueSkyContext =
      mockk<BlueSkyContext> { every { getMentions() } returns mapOf("Test" to mention) }

    val blueSkyMentionCacheService =
      BlueSkyMentionCacheService(blueSkyClient = blueSkyClient, blueSkyContext = blueSkyContext)

    blueSkyMentionCacheService.afterPropertiesSet()
    blueSkyMentionCacheService.loadCache()

    assertEquals(mention, blueSkyMentionCacheService.resolveDidForMention(mention = mention))
  }
}
