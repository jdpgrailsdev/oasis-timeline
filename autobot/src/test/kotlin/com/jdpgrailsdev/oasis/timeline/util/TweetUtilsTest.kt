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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class TweetUtilsTest {
  @Test
  fun testCreateTweetRequest() {
    val text = "some text"
    val inReplyToStatusId = "some id"

    val request = TweetUtils.createTweetRequest(text = text, inReplyToStatusId = inReplyToStatusId)
    assertEquals(text, request.text)
    assertEquals(inReplyToStatusId, request.reply?.inReplyToTweetId)
  }

  @Test
  fun testCreateTweetRequestWithWhitespace() {
    val text = "     some text     "
    val inReplyToStatusId = "some id"

    val request = TweetUtils.createTweetRequest(text = text, inReplyToStatusId = inReplyToStatusId)
    assertEquals(text.trim { it <= ' ' }, request.text)
    assertEquals(inReplyToStatusId, request.reply?.inReplyToTweetId)
  }

  @Test
  fun testCreateTweetRequestNoReply() {
    val text = "some text"

    val request = TweetUtils.createTweetRequest(text = text)
    assertEquals(text, request.text)
    assertNull(request.reply)
  }

  @Test
  fun testConvertPostToTweetCreateRequest() {
    val text = "some text"
    val limit = 200
    val post = Post(text = text, limit = limit)
    val request = post.toTweetCreateRequest()
    assertEquals(text, request.text)
    assertNull(request.reply)
  }

  @Test
  fun testConvertPostToTweetReplies() {
    val limit = 200
    val replyCount = 5
    // Create enough words to equal the limit (limit / word length) then multiply by number of
    // desired replies
    val text = "some ".repeat(limit / 5).repeat(replyCount)
    val inReplyToStatusId = "some id"
    val post = Post(text = text, limit = limit)
    val replies = post.toTweetReplies(inReplyToStatusId = inReplyToStatusId)
    assertEquals(replyCount, replies.size)
  }
}
