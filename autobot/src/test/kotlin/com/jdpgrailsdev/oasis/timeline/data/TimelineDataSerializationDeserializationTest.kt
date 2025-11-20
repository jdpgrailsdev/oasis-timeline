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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import java.io.IOException
import java.nio.charset.Charset

internal class TimelineDataSerializationDeserializationTest {
  private lateinit var objectMapper: ObjectMapper

  @BeforeEach
  fun setup() {
    objectMapper =
      JsonMapper
        .builder()
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .build()
  }

  @Test
  @DisplayName(
    "test that deserialization and serialization of a timeline data event works as expected",
  )
  @Throws(IOException::class)
  fun testDeserializationAndSerialization() {
    val json = loadJson("/json/timelineDataEvent.json")
    val timelineData = objectMapper.readValue(json, TimelineData::class.java)
    Assertions.assertNotNull(timelineData)
    Assertions.assertEquals("January 1", timelineData!!.date)
    Assertions.assertEquals("This is a description of an event 1.", timelineData.description)
    Assertions.assertNull(timelineData.isDisputed)
    Assertions.assertEquals(TimelineDataSource::class.java, timelineData.source.javaClass)
    Assertions.assertEquals("source1", timelineData.source.name)
    Assertions.assertEquals("article1", timelineData.source.title)
    Assertions.assertEquals("http://www.title.com/article1", timelineData.source.url)
    Assertions.assertEquals("Test Event 1", timelineData.title)
    Assertions.assertEquals(TimelineDataType.CERTIFICATIONS, timelineData.type)
    Assertions.assertEquals(2020, timelineData.year)

    val json2 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(timelineData)

    Assertions.assertEquals(json2, json)
  }

  @Test
  @DisplayName(
    (
      "test that deserialization and serialization of a timeline data event with an explicitly set" +
        " disputed field works as expected"
    ),
  )
  @Throws(IOException::class)
  fun testDeserializationAndSerializationWithDisputed() {
    val json = loadJson("/json/timelineDataEventDisputed.json")
    val timelineData = objectMapper.readValue(json, TimelineData::class.java)
    Assertions.assertNotNull(timelineData)
    Assertions.assertEquals("January 1", timelineData!!.date)
    Assertions.assertEquals("This is a description of an event 1.", timelineData.description)
    Assertions.assertEquals(true, timelineData.isDisputed)
    Assertions.assertEquals(TimelineDataSource::class.java, timelineData.source.javaClass)
    Assertions.assertEquals("source1", timelineData.source.name)
    Assertions.assertEquals("article1", timelineData.source.title)
    Assertions.assertEquals("http://www.title.com/article1", timelineData.source.url)
    Assertions.assertEquals("Test Event 1", timelineData.title)
    Assertions.assertEquals(TimelineDataType.CERTIFICATIONS, timelineData.type)
    Assertions.assertEquals(2020, timelineData.year)

    val json2 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(timelineData)

    Assertions.assertEquals(json2, json)
  }

  private fun loadJson(path: String): String {
    val timelineDataEventResource: Resource =
      ClassPathResource(path, Thread.currentThread().getContextClassLoader())

    return timelineDataEventResource.file.readText(charset = Charset.defaultCharset()).trim {
      it <= ' '
    }
  }
}
