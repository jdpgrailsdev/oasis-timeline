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

import static com.jdpgrailsdev.oasis.timeline.util.metrics.MetricConstants.TIMELINE_EVENTS_PUBLISHER_TYPE;

import com.google.common.collect.ImmutableMap;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.Generated;
import com.jdpgrailsdev.oasis.timeline.util.format.EventFormatUtils;
import com.newrelic.api.agent.NewRelic;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Spring scheduler that publishes Mastodon status updates for daily events on a fixed schedule. */
@SuppressFBWarnings("EI_EXPOSE_REP2")
public abstract class TimelineEventScheduler<T> {

  private static final Logger log = LoggerFactory.getLogger(TimelineEventScheduler.class);

  private final DateUtils dateUtils;

  private final EventFormatUtils<T> eventFormatUtils;

  private final MeterRegistry meterRegistry;

  private final TimelineDataLoader timelineDataLoader;

  public TimelineEventScheduler(
      final DateUtils dateUtils,
      final EventFormatUtils<T> eventFormatUtils,
      final MeterRegistry meterRegistry,
      final TimelineDataLoader timelineDataLoader) {
    this.dateUtils = dateUtils;
    this.eventFormatUtils = eventFormatUtils;
    this.meterRegistry = meterRegistry;
    this.timelineDataLoader = timelineDataLoader;
  }

  public T convertEventToType(final TimelineData timelineData) {
    try {
      return eventFormatUtils.generateEvent(
          timelineData, getAdditionalHistoryContext(timelineData));
    } catch (final Exception e) {
      log.error("Unable to generate event for timeline data {}.", timelineData, e);
      noticeError(e, timelineData);
      return null;
    }
  }

  protected List<String> getAdditionalHistoryContext(final TimelineData timelineData) {
    return timelineDataLoader.getAdditionalHistoryContext(timelineData);
  }

  protected DateUtils getDateUtils() {
    return dateUtils;
  }

  protected MeterRegistry getMeterRegistry() {
    return meterRegistry;
  }

  protected abstract TimelineEventsPublisherType getPublisherType();

  /** Publishes each timeline event associated with today's date. */
  public abstract void publishTimelineEvent();

  protected List<T> generateTimelineEvents() {
    final String today = getDateUtils().today();
    log.debug("Fetching timeline events for today's date {}...", today);
    return timelineDataLoader.getHistory(today).stream()
        .map(this::convertEventToType)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  protected void noticeError(final Exception error, final TimelineData timelineData) {
    NewRelic.noticeError(
        error,
        ImmutableMap.of(
            TIMELINE_EVENTS_PUBLISHER_TYPE,
            getPublisherType().name(),
            "timeline_title",
            timelineData.getTitle(),
            "timeline_description",
            timelineData.getDescription(),
            "timeline_date",
            timelineData.getDate(),
            "timeline_type",
            timelineData.getType(),
            "timeline_year",
            timelineData.getYear()));
  }

  @Generated
  protected void handleError(final Throwable throwable) {
    log.error("Unable to publish event.", throwable);
    NewRelic.noticeError(throwable);
  }
}
