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

package com.jdpgrailsdev.oasis.timeline.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyCreateSessionResponse
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyFacetType
import com.jdpgrailsdev.oasis.timeline.config.SocialContext
import com.jdpgrailsdev.oasis.timeline.data.PostException
import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.IContext
import java.io.IOException

internal class BlueSkyPostPublisherServiceTest {
  private lateinit var objectMapper: ObjectMapper

  @BeforeEach
  fun setUp() {
    objectMapper =
      JsonMapper
        .builder()
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .addModule(KotlinModule.Builder().build())
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .build()
  }

  @Test
  fun testPublishingEvents() {
    val additionalTimelineDataResource: Resource =
      ClassPathResource(
        "/json/additionalContextData.json",
        Thread.currentThread().contextClassLoader,
      )
    val timelineDataResource: Resource =
      ClassPathResource("/json/testTimelineData.json", Thread.currentThread().contextClassLoader)
    val resolver =
      mockk<ResourcePatternResolver> {
        every { getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) } returns
          arrayOf(additionalTimelineDataResource)
        every { getResources(TIMELINE_DATA_FILE_LOCATION) } returns arrayOf(timelineDataResource)
      }

    val blueSkyClient =
      mockk<BlueSkyClient> {
        every { createRecord(any(), any()) } returns
          mockk {
            every { cid } returns "cid"
            every { uri } returns "uri"
          }
        every { createSession() } returns
          BlueSkyCreateSessionResponse(
            accessJwt = "token",
            refreshJwt = "refresh",
            handle = "handle",
            did = "did",
          )
      }
    val blueSkyResolverMap =
      mapOf<BlueSkyFacetType, (mention: String) -> String>(BlueSkyFacetType.MENTION to { v -> v })
    val dateUtils = mockk<DateUtils>()
    val meterRegistry =
      mockk<MeterRegistry> {
        every { counter(any(), "target", PostTarget.BLUESKY.name.lowercase()) } returns
          mockk { every { count() } returns 1.0 }
      }
    val socialContexts =
      listOf(
        mockk<SocialContext> {
          every { getHashtags() } returns emptySet()
          every { getMentions() } returns emptyMap()
          every { supports(PostTarget.BLUESKY) } returns true
          every { getUncapitalizeExclusions() } returns emptySet()
        },
      )
    val templateEngine =
      mockk<ITemplateEngine> {
        every { process(any<String>(), any<IContext>()) } returns "This is a template string."
      }
    val postFormatUtils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = socialContexts)
    val timelineDataLoader = TimelineDataLoader(objectMapper, resolver)
    timelineDataLoader.afterPropertiesSet()
    val timelineData = timelineDataLoader.getHistory("January 1")

    val blueSkyPostPublisherService =
      BlueSkyPostPublisherService(
        blueSkyClient = blueSkyClient,
        blueSkyResolverMap = blueSkyResolverMap,
        dateUtils = dateUtils,
        meterRegistry = meterRegistry,
        postFormatUtils = postFormatUtils,
        timelineDataLoader = timelineDataLoader,
      )

    blueSkyPostPublisherService.publishTimelineEvents(timelineData = timelineData)

    verify(exactly = timelineData.size) { blueSkyClient.createRecord(any(), any()) }
  }

  @Test
  fun testPublishingEventsWithReplies() {
    val blueSkyClient =
      mockk<BlueSkyClient> {
        every { createRecord(any(), any()) } returns
          mockk {
            every { cid } returns "cid"
            every { uri } returns "uri"
          }
        every { createSession() } returns
          BlueSkyCreateSessionResponse(
            accessJwt = "token",
            refreshJwt = "refresh",
            handle = "handle",
            did = "did",
          )
      }
    val blueSkyResolverMap =
      mapOf<BlueSkyFacetType, (mention: String) -> String>(BlueSkyFacetType.MENTION to { v -> v })
    val dateUtils = mockk<DateUtils>()
    val meterRegistry =
      mockk<MeterRegistry> {
        every { counter(any(), "target", PostTarget.BLUESKY.name.lowercase()) } returns
          mockk { every { count() } returns 1.0 }
      }
    val socialContexts =
      listOf(
        mockk<SocialContext> {
          every { getHashtags() } returns emptySet()
          every { getMentions() } returns emptyMap()
          every { supports(PostTarget.BLUESKY) } returns true
          every { getUncapitalizeExclusions() } returns emptySet()
        },
      )
    val timelineDataLoader =
      mockk<TimelineDataLoader> { every { getAdditionalHistoryContext(any()) } returns emptyList() }
    val replies = 5
    val timelineData =
      listOf(
        mockk<TimelineData> {
          every { date } returns "September 1"
          every { description } returns
            "some text!".repeat(PostTarget.BLUESKY.limit / 10).repeat(replies)
          every { title } returns "title"
          every { type } returns TimelineDataType.GIGS
          every { year } returns 2024
        },
      )
    val templateEngine =
      mockk<ITemplateEngine> {
        every { process(any<String>(), any<IContext>()) } returns timelineData.first().description
      }
    val postFormatUtils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = socialContexts)

    val blueSkyPostPublisherService =
      BlueSkyPostPublisherService(
        blueSkyClient = blueSkyClient,
        blueSkyResolverMap = blueSkyResolverMap,
        dateUtils = dateUtils,
        meterRegistry = meterRegistry,
        postFormatUtils = postFormatUtils,
        timelineDataLoader = timelineDataLoader,
      )

    blueSkyPostPublisherService.publishTimelineEvents(timelineData = timelineData)

    verify(exactly = replies + 1) { blueSkyClient.createRecord(any(), any()) }
  }

  @Test
  fun testPublishingNoEvents() {
    val blueSkyClient = mockk<BlueSkyClient>()
    val blueSkyResolverMap =
      mapOf<BlueSkyFacetType, (mention: String) -> String>(BlueSkyFacetType.MENTION to { v -> v })
    val dateUtils = mockk<DateUtils>()
    val meterRegistry =
      mockk<MeterRegistry> {
        every { counter(any(), "target", PostTarget.BLUESKY.name.lowercase()) } returns
          mockk { every { count() } returns 1.0 }
      }
    val postFormatUtils = mockk<PostFormatUtils>()
    val timelineDataLoader = mockk<TimelineDataLoader> {}
    val timelineData = emptyList<TimelineData>()

    val blueSkyPostPublisherService =
      BlueSkyPostPublisherService(
        blueSkyClient = blueSkyClient,
        blueSkyResolverMap = blueSkyResolverMap,
        dateUtils = dateUtils,
        meterRegistry = meterRegistry,
        postFormatUtils = postFormatUtils,
        timelineDataLoader = timelineDataLoader,
      )

    blueSkyPostPublisherService.publishTimelineEvents(timelineData = timelineData)

    verify(exactly = 0) { blueSkyClient.createRecord(any(), any()) }
  }

  @Test
  fun testPublishingNullEvents() {
    val blueSkyClient = mockk<BlueSkyClient>()
    val blueSkyResolverMap =
      mapOf<BlueSkyFacetType, (mention: String) -> String>(BlueSkyFacetType.MENTION to { v -> v })
    val dateUtils = mockk<DateUtils>()
    val meterRegistry =
      mockk<MeterRegistry> {
        every { counter(any(), "target", PostTarget.BLUESKY.name.lowercase()) } returns
          mockk { every { count() } returns 1.0 }
      }
    val postFormatUtils =
      mockk<PostFormatUtils> {
        every { generatePost(any(), any(), PostTarget.BLUESKY) } throws
          PostException(message = "test")
      }
    val timelineDataLoader =
      mockk<TimelineDataLoader> { every { getAdditionalHistoryContext(any()) } returns emptyList() }
    val timelineData =
      (1..5).map {
        mockk<TimelineData> {
          every { date } returns "January $it"
          every { description } returns "description$it"
          every { title } returns "title$it"
          every { type } returns TimelineDataType.NOTEWORTHY
          every { year } returns 2020 + it
        }
      }

    val blueSkyPostPublisherService =
      BlueSkyPostPublisherService(
        blueSkyClient = blueSkyClient,
        blueSkyResolverMap = blueSkyResolverMap,
        dateUtils = dateUtils,
        meterRegistry = meterRegistry,
        postFormatUtils = postFormatUtils,
        timelineDataLoader = timelineDataLoader,
      )

    blueSkyPostPublisherService.publishTimelineEvents(timelineData = timelineData)

    verify(exactly = 0) { blueSkyClient.createRecord(any(), any()) }
  }

  @Test
  fun testErrorHandling() {
    val additionalTimelineDataResource: Resource =
      ClassPathResource(
        "/json/additionalContextData.json",
        Thread.currentThread().contextClassLoader,
      )
    val timelineDataResource: Resource =
      ClassPathResource("/json/testTimelineData.json", Thread.currentThread().contextClassLoader)
    val resolver =
      mockk<ResourcePatternResolver> {
        every { getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) } returns
          arrayOf(additionalTimelineDataResource)
        every { getResources(TIMELINE_DATA_FILE_LOCATION) } returns arrayOf(timelineDataResource)
      }

    val blueSkyClient =
      mockk<BlueSkyClient> { every { createSession() } throws IOException("text") }
    val blueSkyResolverMap =
      mapOf<BlueSkyFacetType, (mention: String) -> String>(BlueSkyFacetType.MENTION to { v -> v })
    val dateUtils = mockk<DateUtils>()
    val meterRegistry =
      mockk<MeterRegistry> {
        every { counter(any(), "target", PostTarget.BLUESKY.name.lowercase()) } returns
          mockk { every { count() } returns 1.0 }
      }
    val socialContexts =
      listOf(
        mockk<SocialContext> {
          every { getHashtags() } returns emptySet()
          every { getMentions() } returns emptyMap()
          every { supports(PostTarget.BLUESKY) } returns true
          every { getUncapitalizeExclusions() } returns emptySet()
        },
      )
    val templateEngine =
      mockk<ITemplateEngine> {
        every { process(any<String>(), any<IContext>()) } returns "This is a template string."
      }
    val postFormatUtils =
      PostFormatUtils(textTemplateEngine = templateEngine, socialContexts = socialContexts)
    val timelineDataLoader = TimelineDataLoader(objectMapper, resolver)
    timelineDataLoader.afterPropertiesSet()
    val timelineData = timelineDataLoader.getHistory("January 1")

    val blueSkyPostPublisherService =
      BlueSkyPostPublisherService(
        blueSkyClient = blueSkyClient,
        blueSkyResolverMap = blueSkyResolverMap,
        dateUtils = dateUtils,
        meterRegistry = meterRegistry,
        postFormatUtils = postFormatUtils,
        timelineDataLoader = timelineDataLoader,
      )

    assertDoesNotThrow {
      blueSkyPostPublisherService.publishTimelineEvents(timelineData = timelineData)
    }
  }
}
