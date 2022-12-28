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

package com.jdpgrailsdev.oasis.timeline.schedule;

import static com.jdpgrailsdev.oasis.timeline.util.metrics.MetricConstants.PUBLISH_EXECUTIONS;
import static com.jdpgrailsdev.oasis.timeline.util.metrics.MetricConstants.PUBLISH_TIMER_NAME;
import static com.jdpgrailsdev.oasis.timeline.util.metrics.MetricConstants.TIMELINE_EVENTS_PUBLISHED;
import static com.jdpgrailsdev.oasis.timeline.util.metrics.MetricConstants.TIMELINE_EVENTS_PUBLISHED_FAILURES;
import static com.jdpgrailsdev.oasis.timeline.util.metrics.MetricConstants.TIMELINE_EVENTS_PUBLISHER_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import com.jdpgrailsdev.oasis.timeline.config.TemplateContext;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import com.jdpgrailsdev.oasis.timeline.data.model.mastodon.MastodonStatus;
import com.jdpgrailsdev.oasis.timeline.data.model.mastodon.MastodonStatusUpdate;
import com.jdpgrailsdev.oasis.timeline.mocks.MockMastodonClient;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.MastodonFormatUtils;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

/** Test suite for the {@link MastodonTimelineEventScheduler} class. */
class MastodonTimelineEventSchedulerTests {

  private DateUtils dateUtils;

  private MastodonFormatUtils mastodonFormatUtils;

  private MeterRegistry meterRegistry;

  private Statuses statuses;

  private MastodonClient mastodonClient;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    final ITemplateEngine templateEngine = mock(ITemplateEngine.class);
    final TimelineDataLoader timelineDataLoader = mock(TimelineDataLoader.class);
    final Timer timer = mock(Timer.class);
    final TemplateContext templateContext = mock(TemplateContext.class);
    dateUtils = mock(DateUtils.class);
    meterRegistry = mock(MeterRegistry.class);
    mastodonClient = MockMastodonClient.mock();
    mastodonFormatUtils = new MastodonFormatUtils(templateEngine, templateContext);
    statuses = new Statuses(mastodonClient);
    objectMapper =
        JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    when(dateUtils.today()).thenReturn("January 1");
    when(meterRegistry.counter(anyString(), anyString(), anyString()))
        .thenReturn(mock(Counter.class));
    when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(timer);
    when(templateEngine.process(anyString(), any(IContext.class)))
        .thenReturn("This is a template string.");
    when(timer.record(any(Supplier.class)))
        .thenAnswer(
            invocation -> {
              final Supplier supplier = invocation.getArgument(0);
              return supplier.get();
            });
    when(timelineDataLoader.getHistory(anyString())).thenReturn(List.of());
    when(templateContext.getHashtags()).thenReturn(Set.of("hashtag1", "hashtag2"));
    when(templateContext.getMentions()).thenReturn(Map.of());
    when(templateContext.getUncapitalizeExclusions()).thenReturn(Set.of("Proper Noun"));
  }

  @Test
  @DisplayName(
      "test that when the scheduled task runs, status updates are published for each timeline"
          + " event")
  void testScheduledTask() throws IOException {
    final Resource additionalTimelineDataResource =
        new ClassPathResource(
            "/json/additionalContextData.json", Thread.currentThread().getContextClassLoader());
    final Resource timelineDataResource =
        new ClassPathResource(
            "/json/testTimelineData.json", Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {additionalTimelineDataResource});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});

    loader.afterPropertiesSet();

    final MastodonTimelineEventScheduler scheduler =
        new MastodonTimelineEventScheduler(
            dateUtils, mastodonFormatUtils, statuses, meterRegistry, loader);

    scheduler.publishUpdates();

    verify(meterRegistry, times(1))
        .counter(
            PUBLISH_EXECUTIONS,
            TIMELINE_EVENTS_PUBLISHER_TYPE,
            TimelineEventsPublisherType.MASTODON.name());
    verify(meterRegistry, times(4))
        .counter(
            TIMELINE_EVENTS_PUBLISHED,
            TIMELINE_EVENTS_PUBLISHER_TYPE,
            TimelineEventsPublisherType.MASTODON.name());
    verify(mastodonClient, times(4)).post(anyString(), any());
  }

  @Test
  @DisplayName("test that when no events could be found, nothing is published")
  void testPublishingStatusUpdatesNoEventsFound() {
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);

    when(loader.getHistory(anyString())).thenReturn(List.of());

    final MastodonTimelineEventScheduler scheduler =
        new MastodonTimelineEventScheduler(
            dateUtils, mastodonFormatUtils, statuses, meterRegistry, loader);

    scheduler.publishUpdates();

    verify(mastodonClient, times(0)).post(anyString(), any());
  }

  @Test
  @DisplayName("test that null events are filtered prior to publishing")
  void testHandlingNullEventsDuringPublishingStatus() throws PublishedEventException {
    mastodonFormatUtils = mock(MastodonFormatUtils.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TimelineData timelineData = mock(TimelineData.class);

    when(timelineData.getDate()).thenReturn("date");
    when(timelineData.getDescription()).thenReturn("description");
    when(timelineData.getTitle()).thenReturn("title");
    when(timelineData.getType()).thenReturn(TimelineDataType.GIGS);
    when(timelineData.getYear()).thenReturn(2020);
    when(loader.getHistory(anyString())).thenReturn(List.of(timelineData));
    when(mastodonFormatUtils.generateEvent(any(TimelineData.class), anyList()))
        .thenThrow(PublishedEventException.class);

    final MastodonTimelineEventScheduler scheduler =
        new MastodonTimelineEventScheduler(
            dateUtils, mastodonFormatUtils, statuses, meterRegistry, loader);

    scheduler.publishUpdates();

    verify(mastodonClient, times(0)).post(anyString(), any());
  }

  @Test
  @DisplayName("test that when the scheduled task run a timer metric is recorded")
  void testPublishTweetMetricRecorded() throws IOException {
    final Resource additionalTimelineDataResource =
        new ClassPathResource(
            "/json/additionalContextData.json", Thread.currentThread().getContextClassLoader());
    final Resource timelineDataResource =
        new ClassPathResource(
            "/json/testTimelineData.json", Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {additionalTimelineDataResource});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});

    loader.afterPropertiesSet();

    final MastodonTimelineEventScheduler scheduler =
        new MastodonTimelineEventScheduler(
            dateUtils, mastodonFormatUtils, statuses, meterRegistry, loader);

    scheduler.publishTimelineEvent();

    verify(meterRegistry, times(1))
        .timer(
            PUBLISH_TIMER_NAME,
            TIMELINE_EVENTS_PUBLISHER_TYPE,
            TimelineEventsPublisherType.MASTODON.name());
  }

  @Test
  @DisplayName("test that when conversion of an event to a Tweet fails, the exception is handled")
  void testConvertEventToTweetExceptionHandling() throws PublishedEventException {
    mastodonFormatUtils = mock(MastodonFormatUtils.class);

    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TimelineData timelineData = mock(TimelineData.class);

    when(loader.getAdditionalHistoryContext(any(TimelineData.class))).thenReturn(List.of());
    when(timelineData.getDate()).thenReturn("date");
    when(timelineData.getDescription()).thenReturn("description");
    when(timelineData.getTitle()).thenReturn("title");
    when(timelineData.getType()).thenReturn(TimelineDataType.GIGS);
    when(timelineData.getYear()).thenReturn(2020);
    when(mastodonFormatUtils.generateEvent(any(TimelineData.class), anyList()))
        .thenThrow(PublishedEventException.class);

    final MastodonTimelineEventScheduler scheduler =
        new MastodonTimelineEventScheduler(
            dateUtils, mastodonFormatUtils, statuses, meterRegistry, loader);

    final MastodonStatus mastodonStatus = scheduler.convertEventToType(timelineData);

    assertNull(mastodonStatus, AssertionMessage.NULL.toString());
  }

  @Test
  @DisplayName("test that when the publishing of status updates fails, the exception is handled")
  void testPublishStatusUpdateExceptionHandling() {
    mastodonClient = MockMastodonClient.mockWithException();
    final Statuses statuses = new Statuses(mastodonClient);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final MastodonStatusUpdate statusUpdate = new MastodonStatusUpdate("statusUpdate", null);

    final MastodonTimelineEventScheduler scheduler =
        new MastodonTimelineEventScheduler(
            dateUtils, mastodonFormatUtils, statuses, meterRegistry, loader);

    final Optional<Status> result = scheduler.publishStatusUpdate(statusUpdate);

    verify(meterRegistry, times(1))
        .counter(
            TIMELINE_EVENTS_PUBLISHED_FAILURES,
            TIMELINE_EVENTS_PUBLISHER_TYPE,
            TimelineEventsPublisherType.MASTODON.name());
    assertTrue(result.isEmpty(), AssertionMessage.VALUE.toString());
  }

  @Test
  @DisplayName(
      "test that when a tweet without any replies is published, the single status is returned")
  void testPublishingTweetWithoutReplies() {
    final MastodonStatus status = mock(MastodonStatus.class);
    final MastodonStatusUpdate statusUpdate = new MastodonStatusUpdate("Test Status", null);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);

    when(status.getMainMessage()).thenReturn(statusUpdate);
    when(status.getReplies(anyLong())).thenReturn(List.of(statusUpdate));

    final MastodonTimelineEventScheduler scheduler =
        new MastodonTimelineEventScheduler(
            dateUtils, mastodonFormatUtils, statuses, meterRegistry, loader);

    final Optional<Status> result = scheduler.publishStatus(status);
    assertEquals(
        status.getMainMessage().status(),
        result.orElse(new Status()).getContent(),
        AssertionMessage.VALUE.toString());
  }
}
