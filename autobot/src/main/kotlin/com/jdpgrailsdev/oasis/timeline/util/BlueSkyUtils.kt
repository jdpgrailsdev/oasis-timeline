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
    resolvers: Map<BlueSkyFacetType, (mention: String) -> String?>,
  ): BlueSkyRecord {
    val utf8Text = text.toByteArray(Charsets.UTF_8).toString(Charsets.UTF_8)
    return BlueSkyRecord(
      text = utf8Text,
      createdAt = TIMESTAMP_FORMAT.format(Instant.now()),
      reply = reply,
      facets = createFacets(text = utf8Text, resolvers = resolvers),
    )
  }

  fun createReply(
    rootMessage: BlueSkyReplyPost? = null,
    parentMessage: BlueSkyReplyPost? = null,
  ): BlueSkyReply = BlueSkyReply(root = rootMessage ?: parentMessage, parent = parentMessage ?: rootMessage)

  private fun createFacets(
    text: String,
    resolvers: Map<BlueSkyFacetType, (mention: String) -> String?>,
  ): List<BlueSkyFacet> =
    if (text.isEmpty()) {
      emptyList()
    } else {
      val hashtagMatches = hashtagRegEx.findAll(text)
      val mentionsMatches = mentionsRegEx.findAll(text)
      buildFacets(
        text = text,
        matches = mentionsMatches,
        type = BlueSkyFacetType.MENTION,
        resolvers = resolvers,
      ) +
        buildFacets(
          text = text,
          matches = hashtagMatches,
          type = BlueSkyFacetType.TAG,
          resolvers = resolvers,
        )
    }

  private fun buildFacets(
    text: String,
    matches: Sequence<MatchResult>,
    type: BlueSkyFacetType,
    resolvers: Map<BlueSkyFacetType, (mention: String) -> String?>,
  ): List<BlueSkyFacet> =
    matches
      .map { m ->
        val value = m.value
        val index = text.indexOf(value)
        val facetValue = value.replace(value.first().toString(), "")
        val facetFeature =
          createFacetFeature(type = type, value = facetValue, resolvers = resolvers)
        BlueSkyFacet(
          index = BlueSkyFacetIndex(byteStart = index, byteEnd = (value.length + index)),
          features = if (facetFeature != null) listOf(facetFeature) else emptyList(),
        )
      }.toList()

  private fun createFacetFeature(
    type: BlueSkyFacetType,
    value: String,
    resolvers: Map<BlueSkyFacetType, (mention: String) -> String?>,
  ): BlueSkyFacetFeature? =
    when (type) {
      BlueSkyFacetType.MENTION -> {
        val did = resolvers.getOrDefault(BlueSkyFacetType.MENTION) { v -> v }.invoke(value)
        if (did != null) {
          BlueSkyMentionFacetFeature(did = did)
        } else {
          null
        }
      }
      BlueSkyFacetType.TAG -> BlueSkyTagFacetFeature(tag = value)
    }
}

fun Post.toBlueSkyRecord(resolvers: Map<BlueSkyFacetType, (mention: String) -> String?>): BlueSkyRecord =
  BlueSkyUtils.createRecord(text = this.getMainPost(), resolvers = resolvers)
