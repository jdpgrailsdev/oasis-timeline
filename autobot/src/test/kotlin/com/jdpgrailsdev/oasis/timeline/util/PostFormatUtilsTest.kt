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

import com.jdpgrailsdev.oasis.timeline.config.PostContext
import com.jdpgrailsdev.oasis.timeline.data.PostException
import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataSource
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.platform.commons.util.StringUtils
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.IContext
import java.util.function.Consumer

internal class PostFormatUtilsTest {
  private lateinit var templateEngine: ITemplateEngine

  private lateinit var postContext: PostContext

  private lateinit var utils: PostFormatUtils

  @BeforeEach
  fun setUp() {
    templateEngine =
      mockk<ITemplateEngine> {
        every { process(any<String>(), any<IContext>()) } returns "This is a template string."
      }
    postContext =
      mockk<PostContext> {
        every { getHashtags() } returns setOf("hashtag1", "hashtag2")
        every { getMentions() } returns mapOf("A" to "a")
        every { getUncapitalizeExclusions() } returns setOf("Proper Noun", "Oasis")
        every { supports(any()) } returns true
      }
    utils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = listOf(postContext))
  }

  @ParameterizedTest
  @EnumSource(PostTarget::class)
  @Throws(PostException::class)
  fun testMultipartPost(postTarget: PostTarget) {
    val text = "A word".repeat(postTarget.limit)
    val timelineData =
      mockk<TimelineData> {
        every { date } returns "January 1"
        every { description } returns text
        every { source } returns mockk<TimelineDataSource>()
        every { title } returns "title"
        every { type } returns TimelineDataType.GIGS
        every { year } returns 2020
      }
    templateEngine =
      mockk<ITemplateEngine> { every { process(any<String>(), any<IContext>()) } returns text }
    utils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = listOf(postContext))

    val additionalContext = listOf("additional context", "more context")
    val post =
      utils.generatePost(
        timelineData = timelineData,
        additionalContext = additionalContext,
        postTarget = postTarget,
      )

    assertNotNull(post)
    assertEquals(8, post.getMessages().size)
    post
      .getMessages()
      .forEach(Consumer { message: String -> assertTrue(message.length <= postTarget.limit) })
  }

  @ParameterizedTest
  @EnumSource(PostTarget::class)
  @Throws(PostException::class)
  fun testSinglePartPost(postTarget: PostTarget) {
    val text = "A word"
    val timelineData =
      mockk<TimelineData> {
        every { date } returns "January 1"
        every { description } returns text
        every { source } returns mockk<TimelineDataSource>()
        every { title } returns "title"
        every { type } returns TimelineDataType.GIGS
        every { year } returns 2020
      }
    templateEngine =
      mockk<ITemplateEngine> { every { process(any<String>(), any<IContext>()) } returns text }
    utils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = listOf(postContext))

    val additionalContext = listOf("additional context")
    val post =
      utils.generatePost(
        timelineData = timelineData,
        additionalContext = additionalContext,
        postTarget = postTarget,
      )

    assertNotNull(post)
    assertEquals(1, post.getMessages().size)
    post
      .getMessages()
      .forEach(Consumer { message: String -> assertTrue(message.length <= postTarget.limit) })
  }

  @ParameterizedTest
  @EnumSource(PostTarget::class)
  @Throws(PostException::class)
  fun testSinglePartPostWithoutAdditionalContext(postTarget: PostTarget) {
    val text = "A word"
    val timelineData =
      mockk<TimelineData> {
        every { date } returns "January 1"
        every { description } returns text
        every { source } returns mockk<TimelineDataSource>()
        every { title } returns "title"
        every { type } returns TimelineDataType.GIGS
        every { year } returns 2020
      }
    templateEngine =
      mockk<ITemplateEngine> { every { process(any<String>(), any<IContext>()) } returns text }
    utils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = listOf(postContext))

    val additionalContext = emptyList<String>()
    val post =
      utils.generatePost(
        timelineData = timelineData,
        additionalContext = additionalContext,
        postTarget = postTarget,
      )

    assertNotNull(post)
    assertEquals(1, post.getMessages().size)
    post
      .getMessages()
      .forEach(Consumer { message: String -> assertTrue(message.length <= postTarget.limit) })
  }

  @ParameterizedTest
  @CsvSource(
    "Proper Noun does something., Proper Noun does something, BLUESKY",
    "Proper Noun does something., Proper Noun does something, TWITTER",
    "Proper Noun does something, Proper Noun does something, BLUESKY",
    "Proper Noun does something, Proper Noun does something, TWITTER",
    "This is a sentence., this is a sentence, BLUESKY",
    "This is a sentence., this is a sentence, TWITTER",
    "This is a sentence, this is a sentence, BLUESKY",
    "This is a sentence, this is a sentence, TWITTER",
    "Oasis are the best, Oasis are the best, BLUESKY",
    "Oasis are the best, @Oasis are the best, TWITTER",
    "Sentence with Oasis in it, sentence with Oasis in it, BLUESKY",
    "Sentence with Oasis in it, sentence with @Oasis in it, TWITTER",
    ",,BLUESKY",
    ",,TWITTER",
  )
  fun testPreparingPostDescription(
    description: String?,
    expected: String?,
    postTarget: PostTarget,
  ) {
    val result = utils.prepareDescription(description, postTarget)
    assertEquals(expected, result)
  }

  @ParameterizedTest
  @CsvSource(
    "John Doe did something today., john_doe, johndoe, @johndoe, BLUESKY",
    "later John Doe did it again., john_doe, johndoe, @johndoe, BLUESKY",
    "Jane Doe also did something., john_doe, johndoe,, BLUESKY",
    "John Doe did something today.,,,, BLUESKY",
    "John \"Jdoe\" Doe did something., john_jdoe_doe, johndoe, @johndoe, BLUESKY",
    "The Queen of England, queen_of_england, hrm_uk, @hrm_uk, BLUESKY",
    "John Doe did something today., john_doe, johndoe, @johndoe, TWITTER",
    "later John Doe did it again., john_doe, johndoe, @johndoe, TWITTER",
    "Jane Doe also did something., john_doe, johndoe,, TWITTER",
    "John Doe did something today.,,,, TWITTER",
    "John \"Jdoe\" Doe did something., john_jdoe_doe, johndoe, @johndoe, TWITTER",
    "The Queen of England, queen_of_england, hrm_uk, @hrm_uk, TWITTER",
  )
  fun testGeneratingMentions(
    description: String?,
    mentionKey: String?,
    mentionValue: String?,
    expected: String?,
    postTarget: PostTarget,
  ) {
    val configuredMentions: MutableMap<String, String> = mutableMapOf()
    val postContext =
      mockk<PostContext> {
        every { getMentions() } returns configuredMentions
        every { supports(postTarget) } returns true
      }

    if (StringUtils.isNotBlank(mentionKey) && mentionValue != null) {
      configuredMentions[mentionKey!!] = mentionValue
    }

    utils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = listOf(postContext))

    val mentionsString = utils.generateMentions(description, postTarget)

    assertEquals(normalizeMention(expected), mentionsString)
  }

  @Test
  fun testGenerateMultipleMentions() {
    val postTarget = PostTarget.TWITTER
    val description = "John Doe and Jane Doe went to see Oasis"
    val postContext =
      mockk<PostContext> {
        every { getMentions() } returns mapOf("john_doe" to "johndoe", "jane_doe" to "janedoe")
        every { supports(postTarget) } returns true
      }
    utils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = listOf(postContext))
    val mentions = utils.generateMentions(description = description, postTarget = postTarget)
    assertEquals("@janedoe @johndoe", mentions)
  }

  @Test
  fun testGenerateMentionsNullDescription() {
    val mentionsString = utils.generateMentions(description = null, postTarget = PostTarget.BLUESKY)
    assertNotNull(mentionsString)
    assertTrue(mentionsString.isEmpty())
  }

  @Test
  fun testFormatToken() {
    val token = "token to capitalize"
    val formatted = utils.formatToken(token = token)
    assertEquals("token to capitalize".replaceFirstChar { c -> c.uppercase() }, formatted)
  }

  @Test
  fun testFormatTokenExcluded() {
    val token = "of"
    val formatted = utils.formatToken(token = token)
    assertEquals(token, formatted)
  }

  @Test
  fun testTrimDescription() {
    val description = "  a description  "
    val trimmedDescription = utils.trimDescription(description = description)
    assertEquals(description.trim { it <= ' ' }, trimmedDescription)
  }

  @Test
  fun testTrimDescriptionEndsWithPeriod() {
    val description = "  a description."
    val trimmedDescription = utils.trimDescription(description = description)
    assertEquals(description.replace(".", "").trim { it <= ' ' }, trimmedDescription)
  }

  private fun normalizeMention(mention: String?): String? = if (StringUtils.isBlank(mention)) "" else mention
}
