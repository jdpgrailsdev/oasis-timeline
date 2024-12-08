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

import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReply
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReplyPost
import com.jdpgrailsdev.oasis.timeline.data.Post
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BlueSkyUtilsTest {
  @Test
  fun testCreateReplyFromRootMessage() {
    val cid = "cid"
    val uri = "uri"
    val rootMessage = BlueSkyReplyPost(uri = uri, cid = cid)
    val reply = BlueSkyUtils.createReply(rootMessage = rootMessage)
    assertEquals(rootMessage, reply.root)
    assertEquals(rootMessage, reply.parent)
  }

  @Test
  fun testCreateReplyFromParentMessage() {
    val cid = "cid"
    val uri = "uri"
    val parentMessage = BlueSkyReplyPost(uri = uri, cid = cid)
    val reply = BlueSkyUtils.createReply(parentMessage = parentMessage)
    assertEquals(parentMessage, reply.root)
    assertEquals(parentMessage, reply.parent)
  }

  @Test
  fun testCreateReplyFromRootAndParentMessage() {
    val parentCid = "cid"
    val parentUri = "uri"
    val rootCid = "cidRoot"
    val rootUri = "uriRoot"
    val rootMessage = BlueSkyReplyPost(uri = parentUri, cid = parentCid)
    val parentMessage = BlueSkyReplyPost(uri = rootUri, cid = rootCid)
    val reply = BlueSkyUtils.createReply(rootMessage = rootMessage, parentMessage = parentMessage)
    assertEquals(rootMessage, reply.root)
    assertEquals(parentMessage, reply.parent)
  }

  @Test
  fun testCreateRecordNoReply() {
    val text = "some text"
    val record = BlueSkyUtils.createRecord(text = text)
    assertEquals(text, record.text)
    assertNull(record.reply)
  }

  @Test
  fun testCreateRecordWithReply() {
    val text = "some text"
    val reply = BlueSkyReply()
    val record = BlueSkyUtils.createRecord(text = text, reply = reply)
    assertEquals(text, record.text)
    assertNotNull(record.reply)
  }

  @Test
  fun testConvertPostToBlueSkyRecord() {
    val text = "some text"
    val limit = 200
    val post = Post(text = text, limit = limit)
    val record = post.toBlueskyRecord()
    assertEquals(text, record.text)
    assertNotNull(record.createdAt)
  }
}
