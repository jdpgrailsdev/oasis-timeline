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

package com.jdpgrailsdev.oasis.timeline.util

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.api.Pageable
import social.bigbone.api.entity.Status
import social.bigbone.api.method.AccountMethods
import social.bigbone.api.method.StatusMethods

internal class MastodonApiUtilsTest {
  @Test
  fun testPostStatus() {
    val accountId = "1"
    val request = mockk<MastodonRequest<Status>> { every { execute() } returns mockk<Status>() }
    val statusMethods =
      mockk<StatusMethods> {
        every { postStatus(status = any(), inReplyToId = any()) } returns request
      }
    val client = mockk<MastodonClient> { every { statuses } returns statusMethods }
    val text = "post text"

    val mastodonApiUtils = MastodonApiUtils(accountId = accountId, client = client)

    mastodonApiUtils.postStatus(text = text)

    verify(exactly = 1) { statusMethods.postStatus(status = text) }
  }

  @Test
  fun testPostStatusWithReplyId() {
    val accountId = "1"
    val replyId = "1"
    val request = mockk<MastodonRequest<Status>> { every { execute() } returns mockk<Status>() }
    val statusMethods =
      mockk<StatusMethods> {
        every { postStatus(status = any(), inReplyToId = any()) } returns request
      }
    val client = mockk<MastodonClient> { every { statuses } returns statusMethods }
    val text = "post text"

    val mastodonApiUtils = MastodonApiUtils(accountId = accountId, client = client)

    mastodonApiUtils.postStatus(text = text, replyId = replyId)

    verify(exactly = 1) { statusMethods.postStatus(status = text, inReplyToId = replyId) }
  }

  @Test
  fun testGetPosts() {
    val accountId = "1"
    val status = mockk<Status>()
    val result = mockk<Pageable<Status>> { every { part } returns listOf(status) }
    val request = mockk<MastodonRequest<Pageable<Status>>> { every { execute() } returns result }
    val accountMethods =
      mockk<AccountMethods> {
        every { getStatuses(accountId, any(), any(), any(), any(), any(), any()) } returns request
      }
    val client = mockk<MastodonClient> { every { accounts } returns accountMethods }

    val mastodonApiUtils = MastodonApiUtils(accountId = accountId, client = client)

    val statuses = mastodonApiUtils.getPosts()
    assertEquals(1, statuses.size)
    assertEquals(status, statuses.first())
  }
}
