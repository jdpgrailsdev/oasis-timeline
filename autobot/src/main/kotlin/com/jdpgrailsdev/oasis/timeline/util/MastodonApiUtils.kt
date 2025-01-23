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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import social.bigbone.MastodonClient
import social.bigbone.api.entity.Status

/** Provides methods for performing operations against the Mastodon API. */
@SuppressFBWarnings("EI_EXPOSE_REP2")
class MastodonApiUtils(
  private val accountId: String,
  private val client: MastodonClient,
) {
  /**
   * Posts a new [Status] to Mastodon.
   *
   * @param text The post text.
   * @param inReplyToId The optional main status post ID for reply chaining (defaults to null).
   * @return The [Status] object that represents the successfully posted status.
   */
  fun postStatus(
    text: String,
    replyId: String? = null,
  ): Status = client.statuses.postStatus(status = text, inReplyToId = replyId).execute()

  /**
   * Retrieves the posts for the configured Mastodon account ID.
   *
   * @return The posts for the Mastodon account.
   */
  fun getPosts(): List<Status> =
    client.accounts
      .getStatuses(accountId = accountId)
      .execute()
      .part
}
