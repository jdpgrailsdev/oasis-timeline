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

import com.jdpgrailsdev.oasis.timeline.data.Post
import com.jdpgrailsdev.oasis.timeline.data.matchBlankSpace
import com.twitter.clientlib.model.TweetCreateRequest
import com.twitter.clientlib.model.TweetCreateRequestReply
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

/** Utility singleton that contains various operations related to Twitter content. */
object TweetUtils {
  fun createTweetRequest(
    text: String,
    inReplyToStatusId: String? = null,
  ): TweetCreateRequest {
    val tweetCreateRequest = TweetCreateRequest()
    tweetCreateRequest.text = text.trim(::matchBlankSpace)
    if (!inReplyToStatusId.isNullOrBlank()) {
      val reply = TweetCreateRequestReply()
      reply.inReplyToTweetId = inReplyToStatusId
      tweetCreateRequest.reply = reply
    }

    return tweetCreateRequest
  }
}

fun Post.toTweetCreateRequest(): TweetCreateRequest = TweetUtils.createTweetRequest(this.getMainPost())

@SuppressFBWarnings("BC_BAD_CAST_TO_ABSTRACT_COLLECTION")
fun Post.toTweetReplies(inReplyToStatusId: String): List<TweetCreateRequest> =
  this.getReplies().map { m -> TweetUtils.createTweetRequest(m, inReplyToStatusId) }
