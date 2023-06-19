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

package com.jdpgrailsdev.oasis.timeline.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class TimelineDataSerializationDeserializationTests {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper =
        JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();
  }

  @Test
  @DisplayName(
      "test that deserialization and serialization of a timeline data event works as expected")
  void testDeserializationAndSerialization() throws IOException {
    final Resource timelineDataEventResource =
        new ClassPathResource(
            "/json/timelineDataEvent.json", Thread.currentThread().getContextClassLoader());

    final String json =
        FileUtils.readFileToString(timelineDataEventResource.getFile(), Charset.defaultCharset())
            .trim();

    final TimelineData timelineData = objectMapper.readValue(json, TimelineData.class);
    assertNotNull(timelineData);
    assertEquals(timelineData.getDate(), "January 1");
    assertEquals(timelineData.getDescription(), "This is a description of an event 1.");
    assertNull(timelineData.isDisputed());
    assertEquals(timelineData.getSource().getClass(), TimelineDataSource.class);
    assertEquals(timelineData.getSource().getName(), "source1");
    assertEquals(timelineData.getSource().getTitle(), "article1");
    assertEquals(timelineData.getSource().getUrl(), "http://www.title.com/article1");
    assertEquals(timelineData.getTitle(), "Test Event 1");
    assertEquals(timelineData.getType(), TimelineDataType.CERTIFICATIONS);
    assertEquals(timelineData.getYear(), 2020);

    final String json2 =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(timelineData);

    assertEquals(json2, json);
  }

  @Test
  @DisplayName(
      "test that deserialization and serialization of a timeline data event with an explicitly set"
          + " disputed field works as expected")
  void testDeserializationAndSerializationWithDisputed() throws IOException {
    final Resource timelineDataEventResource =
        new ClassPathResource(
            "/json/timelineDataEventDisputed.json", Thread.currentThread().getContextClassLoader());

    final String json =
        FileUtils.readFileToString(timelineDataEventResource.getFile(), Charset.defaultCharset())
            .trim();

    final TimelineData timelineData = objectMapper.readValue(json, TimelineData.class);
    assertNotNull(timelineData);
    assertEquals("January 1", timelineData.getDate());
    assertEquals("This is a description of an event 1.", timelineData.getDescription());
    assertEquals(true, timelineData.isDisputed());
    assertEquals(TimelineDataSource.class, timelineData.getSource().getClass());
    assertEquals("source1", timelineData.getSource().getName());
    assertEquals("article1", timelineData.getSource().getTitle());
    assertEquals("http://www.title.com/article1", timelineData.getSource().getUrl());
    assertEquals("Test Event 1", timelineData.getTitle());
    assertEquals(TimelineDataType.CERTIFICATIONS, timelineData.getType());
    assertEquals(2020, timelineData.getYear());

    final String json2 =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(timelineData);

    assertEquals(json2, json);
  }
}
