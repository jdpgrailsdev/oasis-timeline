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

import com.jdpgrailsdev.oasis.timeline.client.BlueSkyFacet
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyFacetFeature
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyFacetIndex
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyFacetType
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyMentionFacetFeature
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyRecord
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReply
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReplyPost
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyTagFacetFeature
import com.jdpgrailsdev.oasis.timeline.data.Post
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Utility singleton that contains various operations related to Bluesky content. */
object BlueSkyUtils {
  private val hashtagRegEx = "\\B(#[a-zA-Z0-9.-]+\\b)(?!;)".toRegex()
  private val mentionsRegEx = "\\B(@[a-zA-Z0-9.-]+\\b)(?!;)".toRegex()
  private val TIMESTAMP_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").withZone(ZoneId.of("GMT"))

  fun createRecord(
    text: String,
    reply: BlueSkyReply? = null,
  ): BlueSkyRecord =
    BlueSkyRecord(
      text = text,
      createdAt = TIMESTAMP_FORMAT.format(Instant.now()),
      reply = reply,
      facets = createFacets(text = text),
    )

  fun createReply(
    rootMessage: BlueSkyReplyPost? = null,
    parentMessage: BlueSkyReplyPost? = null,
  ): BlueSkyReply = BlueSkyReply(root = rootMessage ?: parentMessage, parent = parentMessage ?: rootMessage)

  private fun createFacets(text: String): List<BlueSkyFacet> =
    if (text.isEmpty()) {
      emptyList()
    } else {
      val hashtagMatches = hashtagRegEx.findAll(text)
      val mentionsMatches = mentionsRegEx.findAll(text)
      buildFacets(text = text, matches = mentionsMatches, type = BlueSkyFacetType.MENTION) +
        buildFacets(text = text, matches = hashtagMatches, type = BlueSkyFacetType.TAG)
    }

  private fun buildFacets(
    text: String,
    matches: Sequence<MatchResult>,
    type: BlueSkyFacetType,
  ): List<BlueSkyFacet> =
    matches
      .map { m ->
        val value = m.value
        val index = text.indexOf(value)
        val facetValue = value.replace(value.first().toString(), "")
        BlueSkyFacet(
          index = BlueSkyFacetIndex(byteStart = index, byteEnd = (value.length + index)),
          features = listOf(createFacetFeature(type = type, value = facetValue)),
        )
      }.toList()

  private fun createFacetFeature(
    type: BlueSkyFacetType,
    value: String,
  ): BlueSkyFacetFeature =
    when (type) {
      BlueSkyFacetType.MENTION -> BlueSkyMentionFacetFeature(did = value)
      BlueSkyFacetType.TAG -> BlueSkyTagFacetFeature(tag = value)
    }
}

fun Post.toBlueskyRecord(): BlueSkyRecord = BlueSkyUtils.createRecord(text = this.getMainPost())
