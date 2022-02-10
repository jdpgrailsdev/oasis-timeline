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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

class TimelineDataLoaderTests {

  private static final String ADDITIONAL_CONTEXT_DATA_FILE = "/json/additionalContextData.json";

  private static final String TIMELINE_DATA_FILE = "/json/testTimelineData.json";

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
      "test that when the timeline data is loaded on bean creation, the timeline data field is"
          + " populated")
  void testLoadingTimelineDataOnCreation() throws IOException {
    final Resource additionalTimelineDataResource =
        new ClassPathResource(
            ADDITIONAL_CONTEXT_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final Resource timelineDataResource =
        new ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {additionalTimelineDataResource});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});

    loader.afterPropertiesSet();

    assertNotNull(loader.getTimelineData(), AssertionMessage.NON_NULL.toString());
    assertEquals(9, loader.getTimelineData().size(), AssertionMessage.SIZE.toString());
    assertNotNull(loader.getTimelineData().get(0).getDate(), AssertionMessage.NON_NULL.toString());
    assertNotNull(
        loader.getTimelineData().get(0).getDescription(), AssertionMessage.NON_NULL.toString());
    assertNotNull(
        loader.getTimelineData().get(0).getSource(), AssertionMessage.NON_NULL.toString());
    assertNotNull(
        loader.getTimelineData().get(0).getSource().getName(),
        AssertionMessage.NON_NULL.toString());
    assertNotNull(
        loader.getTimelineData().get(0).getSource().getTitle(),
        AssertionMessage.NON_NULL.toString());
    assertNotNull(
        loader.getTimelineData().get(0).getSource().getUrl(), AssertionMessage.NON_NULL.toString());
    assertNotNull(loader.getTimelineData().get(0).getTitle(), AssertionMessage.NON_NULL.toString());
    assertNotNull(loader.getTimelineData().get(0).getType(), AssertionMessage.NON_NULL.toString());
    assertNotNull(loader.getTimelineData().get(0).getYear(), AssertionMessage.NON_NULL.toString());
  }

  @Test
  @DisplayName(
      "test that when the timeline data is filtered to a given day, the correct entries are"
          + " returned")
  void testFilterByDate() throws IOException {
    final Resource additionalTimelineDataResource =
        new ClassPathResource(
            ADDITIONAL_CONTEXT_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final Resource timelineDataResource =
        new ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);
    final String today = "January 1";

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {additionalTimelineDataResource});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});

    loader.afterPropertiesSet();
    final List<TimelineData> result = loader.getHistory(today);

    assertEquals(4, result.size(), AssertionMessage.SIZE.toString());
  }

  @Test
  @DisplayName(
      "test that when the timeline data is filtered to a given day and the timeline data has"
          + " additional context, the additional context can be retrieved")
  void testFilterByDateAdditionalContext() throws IOException {
    final Resource additionalTimelineDataResource =
        new ClassPathResource(
            ADDITIONAL_CONTEXT_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final Resource timelineDataResource =
        new ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);
    final String today = "January 1";

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {additionalTimelineDataResource});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});

    loader.afterPropertiesSet();
    final List<TimelineData> result = loader.getHistory(today);

    assertEquals(4, result.size(), AssertionMessage.SIZE.toString());

    result.forEach(
        timelineData -> {
          final List<String> additional = loader.getAdditionalHistoryContext(timelineData);
          if (TimelineDataType.GIGS == timelineData.getType()) {
            assertNotNull(additional, AssertionMessage.NON_NULL.toString());
            assertEquals(3, additional.size(), AssertionMessage.SIZE.toString());
            assertEquals(
                List.of("Song 1", "Song 2", "Song 3"),
                additional,
                AssertionMessage.VALUE.toString());
          } else {
            assertNotNull(additional, AssertionMessage.NON_NULL.toString());
            assertEquals(0, additional.size(), AssertionMessage.SIZE.toString());
          }
        });
  }

  @Test
  @DisplayName(
      "test that when a match is found in the additional data, the additional data is returned")
  void testAdditionalData() throws IOException {
    final Resource additionalTimelineDataResource =
        new ClassPathResource(
            ADDITIONAL_CONTEXT_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final Resource timelineDataResource =
        new ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);
    final TimelineData timelineData = mock(TimelineData.class);
    final String today = "January 1";

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {additionalTimelineDataResource});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});
    when(timelineData.getDate()).thenReturn(today);
    when(timelineData.getYear()).thenReturn(2020);
    when(timelineData.getType()).thenReturn(TimelineDataType.GIGS);

    loader.afterPropertiesSet();

    final List<String> additionalContext = loader.getAdditionalHistoryContext(timelineData);

    assertNotNull(additionalContext, AssertionMessage.NON_NULL.toString());
    assertEquals(3, additionalContext.size(), AssertionMessage.SIZE.toString());
    assertEquals("Song 1", additionalContext.get(0), AssertionMessage.VALUE.toString());
    assertEquals("Song 2", additionalContext.get(1), AssertionMessage.VALUE.toString());
    assertEquals("Song 3", additionalContext.get(2), AssertionMessage.VALUE.toString());
  }

  @Test
  @DisplayName(
      "test that when the timeline data file is unable to be located, an exception is thrown")
  void testMissingTimelineDataFile() throws IOException {
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);

    when(resolver.getResources(anyString())).thenReturn(new Resource[] {});

    Assertions.assertThrows(FileNotFoundException.class, loader::afterPropertiesSet);
  }

  @Test
  @DisplayName(
      "test that when the additional timeline data file is unable to be located, an exception is"
          + " thrown")
  void testMissingAdditionalContextData() throws IOException {
    final Resource timelineDataResource =
        new ClassPathResource(TIMELINE_DATA_FILE, Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});

    Assertions.assertThrows(FileNotFoundException.class, loader::afterPropertiesSet);
  }
}
