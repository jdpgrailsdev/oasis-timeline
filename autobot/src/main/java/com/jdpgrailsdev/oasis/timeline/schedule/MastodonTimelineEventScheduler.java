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

import com.google.common.annotations.VisibleForTesting;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.model.mastodon.MastodonStatus;
import com.jdpgrailsdev.oasis.timeline.data.model.mastodon.MastodonStatusUpdate;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.MastodonFormatUtils;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.entity.Status.Visibility;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

/** Spring scheduler that publishes Mastodon status updates for daily events on a fixed schedule. */
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class MastodonTimelineEventScheduler extends TimelineEventScheduler<MastodonStatus> {

  private static final Logger log = LoggerFactory.getLogger(MastodonTimelineEventScheduler.class);

  private final Statuses mastodonStatusesApi;

  /**
   * Constructs a new {@link MastodonTimelineEventScheduler} instance.
   *
   * @param dateUtils {@link DateUtils} used to format date strings.
   * @param mastodonFormatUtils A {@link MastodonFormatUtils} used to format messages.
   * @param mastodonStatusesApi A {@link Statuses} instance used to post updates to Mastodon.
   * @param meterRegistry {@link MeterRegistry} used to record metrics.
   * @param timelineDataLoader {@link TimelineDataLoader} used to fetch timeline data events.
   */
  public MastodonTimelineEventScheduler(
      final DateUtils dateUtils,
      final MastodonFormatUtils mastodonFormatUtils,
      final Statuses mastodonStatusesApi,
      final MeterRegistry meterRegistry,
      final TimelineDataLoader timelineDataLoader) {
    super(dateUtils, mastodonFormatUtils, meterRegistry, timelineDataLoader);
    this.mastodonStatusesApi = mastodonStatusesApi;
  }

  @Override
  public TimelineEventsPublisherType getPublisherType() {
    return TimelineEventsPublisherType.MASTODON;
  }

  @Scheduled(cron = "0 30 5 * * *")
  @Override
  public void publishTimelineEvent() {
    getMeterRegistry()
        .timer(PUBLISH_TIMER_NAME, TIMELINE_EVENTS_PUBLISHER_TYPE, getPublisherType().name())
        .record(
            () -> {
              publishUpdates();
              log.debug("Execution of scheduled publish of timeline events completed.");
            });
  }

  @VisibleForTesting
  protected void publishUpdates() {
    log.debug("Executing scheduled publish of timeline tweets...");
    getMeterRegistry()
        .counter(PUBLISH_EXECUTIONS, TIMELINE_EVENTS_PUBLISHER_TYPE, getPublisherType().name())
        .count();

    final List<MastodonStatus> statuses = generateTimelineEvents();

    if (!CollectionUtils.isEmpty(statuses)) {
      Flux.fromStream(statuses.stream())
          .doOnError(this::handleError)
          .map(this::publishStatus)
          .blockLast();
    } else {
      log.debug("Did not find any timeline events for date '{}'.", getDateUtils().today());
    }
  }

  @VisibleForTesting
  protected Optional<Status> publishStatus(final MastodonStatus status) {
    final MastodonStatusUpdate mainStatus = status.getMainMessage();

    // Publish the main event first
    final Optional<Status> statuses = publishStatusUpdate(mainStatus);

    // If successful, reply to the main event with the overflow.
    if (statuses.isPresent()) {
      final List<MastodonStatusUpdate> replies = status.getReplies(statuses.get().getId());
      return CollectionUtils.isEmpty(replies)
          ? statuses
          : Flux.fromIterable(replies).map(this::publishStatusUpdate).blockLast();
    } else {
      return Optional.empty();
    }
  }

  protected Optional<Status> publishStatusUpdate(final MastodonStatusUpdate statusUpdate) {
    try {
      final Status status =
          mastodonStatusesApi
              .postStatus(
                  statusUpdate.status(),
                  statusUpdate.inReplyToId(),
                  null,
                  false,
                  null,
                  Visibility.Public)
              .execute();
      getMeterRegistry()
          .counter(
              TIMELINE_EVENTS_PUBLISHED, TIMELINE_EVENTS_PUBLISHER_TYPE, getPublisherType().name())
          .count();
      return Optional.ofNullable(status);
    } catch (final Mastodon4jRequestException e) {
      handleError(e);
      getMeterRegistry()
          .counter(
              TIMELINE_EVENTS_PUBLISHED_FAILURES,
              TIMELINE_EVENTS_PUBLISHER_TYPE,
              getPublisherType().name())
          .count();
      return Optional.empty();
    }
  }
}
