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

package com.jdpgrailsdev.oasis.timeline.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import java.io.FileNotFoundException
import java.io.IOException

private const val ADDITIONAL_CONTEXT_DATA_FILE: String = "/json/additionalContextData.json"
private const val TIMELINE_DATA_FILE: String = "/json/testTimelineData.json"

internal class TimelineDataLoaderTest {
  private lateinit var objectMapper: ObjectMapper

  @BeforeEach
  fun setup() {
    objectMapper =
      JsonMapper
        .builder()
        .addModule(KotlinModule.Builder().build())
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .defaultPropertyInclusion(
          JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.ALWAYS),
        ).build()
  }

  @Test
  @DisplayName(
    (
      "test that when the timeline data is loaded on bean creation, the timeline data field is" +
        " populated"
    ),
  )
  @Throws(IOException::class)
  fun testLoadingTimelineDataOnCreation() {
    val additionalTimelineDataResource: Resource =
      ClassPathResource(
        ADDITIONAL_CONTEXT_DATA_FILE,
        Thread.currentThread().getContextClassLoader(),
      )
    val timelineDataResource: Resource =
      ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader())
    val resolver: ResourcePatternResolver =
      mockk {
        every { getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) } returns
          arrayOf(additionalTimelineDataResource)
        every { getResources(TIMELINE_DATA_FILE_LOCATION) } returns arrayOf(timelineDataResource)
      }
    val loader = TimelineDataLoader(objectMapper, resolver)

    loader.afterPropertiesSet()

    Assertions.assertNotNull(loader.getTimelineData())
    Assertions.assertEquals(10, loader.getTimelineData().size)
    Assertions.assertNotNull(loader.getTimelineData().first().date)
    Assertions.assertNotNull(loader.getTimelineData().first().description)
    Assertions.assertNotNull(loader.getTimelineData().first().source)
    Assertions.assertNotNull(
      loader
        .getTimelineData()
        .first()
        .source.name,
    )
    Assertions.assertNotNull(
      loader
        .getTimelineData()
        .first()
        .source.title,
    )
    Assertions.assertNotNull(
      loader
        .getTimelineData()
        .first()
        .source.url,
    )
    Assertions.assertNotNull(loader.getTimelineData().first().title)
    Assertions.assertNotNull(loader.getTimelineData().first().type)
    Assertions.assertNotNull(loader.getTimelineData().first().year)
  }

  @Test
  @DisplayName(
    (
      "test that when the timeline data is filtered to a given day, the correct entries are" +
        " returned"
    ),
  )
  @Throws(IOException::class)
  fun testFilterByDate() {
    val additionalTimelineDataResource: Resource =
      ClassPathResource(
        ADDITIONAL_CONTEXT_DATA_FILE,
        Thread.currentThread().getContextClassLoader(),
      )
    val timelineDataResource: Resource =
      ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader())
    val resolver: ResourcePatternResolver =
      mockk {
        every { getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) } returns
          arrayOf(additionalTimelineDataResource)
        every { getResources(TIMELINE_DATA_FILE_LOCATION) } returns arrayOf(timelineDataResource)
      }
    val loader = TimelineDataLoader(objectMapper, resolver)
    val today = "January 1"

    loader.afterPropertiesSet()
    val result = loader.getHistory(today)

    Assertions.assertEquals(4, result.size)
  }

  @Test
  @DisplayName(
    (
      "test that when the timeline data is filtered to a given day and the timeline data has" +
        " additional context, the additional context can be retrieved"
    ),
  )
  @Throws(IOException::class)
  fun testFilterByDateAdditionalContext() {
    val additionalTimelineDataResource: Resource =
      ClassPathResource(
        ADDITIONAL_CONTEXT_DATA_FILE,
        Thread.currentThread().getContextClassLoader(),
      )
    val timelineDataResource: Resource =
      ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader())
    val resolver: ResourcePatternResolver =
      mockk {
        every { getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) } returns
          arrayOf(additionalTimelineDataResource)
        every { getResources(TIMELINE_DATA_FILE_LOCATION) } returns arrayOf(timelineDataResource)
      }
    val loader = TimelineDataLoader(objectMapper, resolver)
    val today = "January 1"

    loader.afterPropertiesSet()
    val result = loader.getHistory(today)

    Assertions.assertEquals(4, result.size)

    result.forEach { timelineData: TimelineData ->
      val additional = loader.getAdditionalHistoryContext(timelineData)
      if (TimelineDataType.GIGS == timelineData.type) {
        Assertions.assertNotNull(additional)
        Assertions.assertEquals(3, additional.size)
        Assertions.assertEquals(listOf("Song 1", "Song 2", "Song 3"), additional)
      } else {
        Assertions.assertNotNull(additional)
        Assertions.assertEquals(0, additional.size)
      }
    }
  }

  @Test
  @DisplayName(
    "test that when a match is found in the additional data, the additional data is returned",
  )
  @Throws(IOException::class)
  fun testAdditionalData() {
    val today = "January 1"
    val additionalTimelineDataResource: Resource =
      ClassPathResource(
        ADDITIONAL_CONTEXT_DATA_FILE,
        Thread.currentThread().getContextClassLoader(),
      )
    val timelineDataResource: Resource =
      ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader())
    val resolver: ResourcePatternResolver =
      mockk {
        every { getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) } returns
          arrayOf(additionalTimelineDataResource)
        every { getResources(TIMELINE_DATA_FILE_LOCATION) } returns arrayOf(timelineDataResource)
      }
    val loader = TimelineDataLoader(objectMapper, resolver)
    val timelineData: TimelineData =
      mockk {
        every { date } returns today
        every { year } returns 2020
        every { type } returns TimelineDataType.GIGS
      }

    loader.afterPropertiesSet()

    val additionalContext = loader.getAdditionalHistoryContext(timelineData)

    Assertions.assertNotNull(additionalContext)
    Assertions.assertEquals(3, additionalContext.size)
    Assertions.assertEquals("Song 1", additionalContext[0])
    Assertions.assertEquals("Song 2", additionalContext[1])
    Assertions.assertEquals("Song 3", additionalContext[2])
  }

  @Test
  @DisplayName(
    "test that when the timeline data file is unable to be located, an exception is thrown",
  )
  @Throws(IOException::class)
  fun testMissingTimelineDataFile() {
    val resolver: ResourcePatternResolver =
      mockk {
        every { getResources(any()) } returns arrayOf()
      }
    val loader = TimelineDataLoader(objectMapper, resolver)

    Assertions.assertThrows(FileNotFoundException::class.java) { loader.afterPropertiesSet() }
  }

  @Test
  @DisplayName(
    (
      "test that when the additional timeline data file is unable to be located, an exception is" +
        " thrown"
    ),
  )
  @Throws(IOException::class)
  fun testMissingAdditionalContextData() {
    val timelineDataResource: Resource =
      ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader())
    val resolver: ResourcePatternResolver =
      mockk {
        every { getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) } returns arrayOf()
        every { getResources(TIMELINE_DATA_FILE_LOCATION) } returns arrayOf(timelineDataResource)
      }
    val loader = TimelineDataLoader(objectMapper, resolver)

    Assertions.assertThrows(FileNotFoundException::class.java) { loader.afterPropertiesSet() }
  }
}
