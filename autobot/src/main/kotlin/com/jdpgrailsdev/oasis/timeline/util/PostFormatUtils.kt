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

import com.google.common.collect.Sets
import com.jdpgrailsdev.oasis.timeline.config.SocialContext
import com.jdpgrailsdev.oasis.timeline.data.Post
import com.jdpgrailsdev.oasis.timeline.data.PostException
import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.util.StringUtils
import org.thymeleaf.ITemplateEngine
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

private val EXCLUDED_TOKENS: Collection<String> = Sets.newHashSet("of")
private const val NICKNAME_PATTERN: String = "(\\w+\\s)(\\w+)(\\s\\w+)"

/**
 * Collection of utlities related to the formatting of timeline events into social network posts.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
class PostFormatUtils(
  private val textTemplateEngine: ITemplateEngine,
  private val socialContexts: List<SocialContext>,
) {
  /**
   * Generates a post.
   *
   * @param timelineData [TimelineData] event to be converted to a post.
   * @param additionalContext List of additional context to be included in the post.
   * @param postTarget The target social network for the post.
   * @return The generated post.
   * @throws PostException if unable to generate the post.
   */
  @Throws(PostException::class)
  fun generatePost(
    timelineData: TimelineData,
    additionalContext: List<String>,
    postTarget: PostTarget,
  ): Post =
    generatePost(
      description = timelineData.description,
      timelineDataType = timelineData.type,
      year = timelineData.year,
      postTarget = postTarget,
      additionalContext = additionalContext,
    )

  /**
   * Generates a post.
   *
   * @param description The prose of the post.
   * @param timelineDataType The type of event.
   * @param year The year of the event.
   * @param postTarget The target social network for the post.
   * @param additionalContext List of additional context to be included in the post.
   * @return The generated post.
   * @throws PostException if unable to generate the post.
   */
  @JvmOverloads
  @Throws(PostException::class)
  fun generatePost(
    description: String,
    timelineDataType: TimelineDataType,
    year: Int,
    postTarget: PostTarget,
    additionalContext: List<String> = emptyList(),
  ): Post {
    val context =
      ContextBuilder()
        .withAdditionalContext(additionalContext.joinToString(separator = ", ").trim { it <= ' ' })
        .withDescription(prepareDescription(description, postTarget))
        .withHashtags(
          getSupportedSocialContext(postTarget)
            .map { it.getHashtags() }
            .flatten()
            .map { h -> "#$h" }
            .joinToString(separator = " "),
        ).withMentions(generateMentions(description, postTarget))
        .withSupportsUnicode21(postTarget.supportsUnicode21)
        .withType(timelineDataType)
        .withYear(year)
        .build()

    val text = textTemplateEngine.process(getTemplate(postTarget = postTarget), context)
    return Post.createPost(text = text, limit = postTarget.limit)
  }

  fun generateMentions(
    description: String?,
    postTarget: PostTarget,
  ): String {
    val mentions: MutableList<String> = mutableListOf()
    getSupportedSocialContext(postTarget)
      .flatMap { it.getMentions().entries }
      .map { it.key to it.value }
      .forEach { (key, value) ->
        logger.debug { "Converting key '$key' into a searchable name..." }
        val name =
          key
            .split("_".toRegex())
            .dropLastWhile { it.isEmpty() }
            .joinToString(" ", transform = this::formatToken)
        val nameWithQuotes = name.replace(NICKNAME_PATTERN.toRegex(), "$1\"$2\"$3")
        logger.debug { "Looking for name '$name' in description '$description'..." }
        description?.let {
          if (description.contains(name) || description.contains(nameWithQuotes)) {
            logger.debug { "Match found. Adding '@$value' to list of mentions..." }
            mentions.add("@$value")
          }
        }
      }
    return mentions
      .sortedBy { m -> m.lowercase() }
      .joinToString(separator = " ", transform = this::formatToken)
  }

  fun prepareDescription(
    description: String?,
    postTarget: PostTarget,
  ): String? =
    if (StringUtils.hasText(description)) {
      Mono
        .just<String?>(description!!)
        .map { d -> this.trimDescription(d) }
        .map { d: String ->
          val s: String = this.uncapitalizeDescription(d, postTarget)
          if (postTarget == PostTarget.TWITTER) {
            s.replace("Oasis".toRegex(), "@Oasis")
          } else {
            s
          }
        }.block()
    } else {
      description
    }

  internal fun formatToken(token: String): String =
    if (!EXCLUDED_TOKENS.contains(token)) {
      StringUtils.capitalize(token)
    } else {
      token
    }

  internal fun trimDescription(description: String): String =
    if (description.endsWith(".")) {
      description.substring(0, description.length - 1).trim { it <= ' ' }
    } else {
      description.trim { it <= ' ' }
    }

  private fun uncapitalizeDescription(
    description: String,
    postTarget: PostTarget,
  ): String =
    if (
      getSupportedSocialContext(postTarget)
        .flatMap { it.getUncapitalizeExclusions() }
        .none(description::startsWith)
    ) {
      StringUtils.uncapitalize(description)
    } else {
      description
    }

  private fun getSupportedSocialContext(postTarget: PostTarget) = socialContexts.asSequence().filter { it.supports(postTarget) }

  private fun getTemplate(postTarget: PostTarget): String = postTarget.name.lowercase()
}
