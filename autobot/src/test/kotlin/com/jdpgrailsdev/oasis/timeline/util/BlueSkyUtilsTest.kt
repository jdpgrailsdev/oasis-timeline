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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyFacetType
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyMentionFacetFeature
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReply
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReplyPost
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyTagFacetFeature
import com.jdpgrailsdev.oasis.timeline.data.Post
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
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
    val record = BlueSkyUtils.createRecord(text = text, resolvers = emptyMap())
    assertEquals(text, record.text)
    assertNull(record.reply)
  }

  @Test
  fun testCreateRecordWithReply() {
    val text = "some text"
    val reply = BlueSkyReply()
    val record = BlueSkyUtils.createRecord(text = text, reply = reply, resolvers = emptyMap())
    assertEquals(text, record.text)
    assertNotNull(record.reply)
  }

  @Test
  fun testConvertPostToBlueSkyRecord() {
    val text = "some text"
    val limit = 200
    val post = Post(text = text, limit = limit)
    val record = post.toBlueSkyRecord(resolvers = emptyMap())
    assertEquals(text, record.text)
    assertNotNull(record.createdAt)
  }

  @Test
  fun testCreateRecordWithFacets() {
    val did = "did:plc:2kfn5kwq4dqzncgv2g2tqmii"
    val mention = "test.bsky.social"
    val text = "Some text with @$mention and #tag1 and #tag2"

    val blueSkyResolverMap =
      mapOf<BlueSkyFacetType, (mention: String) -> String>(BlueSkyFacetType.MENTION to { v -> did })

    val record = BlueSkyUtils.createRecord(text = text, resolvers = blueSkyResolverMap)

    val mapper =
      JsonMapper
        .builder()
        .addModule(JavaTimeModule())
        .addModule(KotlinModule.Builder().build())
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .build()
    println(mapper.writeValueAsString(record))
    assertEquals(text, record.text)
    assertNotNull(record.createdAt)
    assertEquals(3, record.facets.size)
    assertEquals(
      2,
      record.facets
        .filter { f ->
          f.features.find { feature -> feature.getType() == BlueSkyFacetType.TAG.type } != null
        }.size,
    )
    assertEquals(
      1,
      record.facets
        .filter { f ->
          f.features.find { feature -> feature.getType() == BlueSkyFacetType.MENTION.type } != null
        }.size,
    )
    assertEquals(
      did,
      (
        record.facets
          .first { f ->
            f.features.find { feature -> feature.getType() == BlueSkyFacetType.MENTION.type } !=
              null
          }.features
          .first() as BlueSkyMentionFacetFeature
      ).did,
    )
    val tagFacets =
      record.facets.filter { f ->
        f.features.find { feature -> feature.getType() == BlueSkyFacetType.TAG.type } != null
      }
    val tags =
      (tagFacets.flatMap { t -> t.features.map { f -> (f as BlueSkyTagFacetFeature).tag } })
    assertTrue(tags.contains("tag1"))
    assertTrue(tags.contains("tag2"))
  }
}
