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
import com.google.common.collect.ImmutableMap;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.model.twitter.Tweet;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.TweetFormatUtils;
import com.newrelic.api.agent.NewRelic;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.Status;
import twitter4j.v1.StatusUpdate;

/** Spring scheduler that publishes tweets for daily events on a fixed schedule. */
public class TwitterTimelineEventScheduler extends TimelineEventScheduler<Tweet> {

  private static final Logger log = LoggerFactory.getLogger(TwitterTimelineEventScheduler.class);

  private final Twitter twitterApi;

  /**
   * Constructs a new scheduler.
   *
   * @param dateUtils {@link DateUtils} used to format date strings.
   * @param meterRegistry {@link MeterRegistry} used to record metrics.
   * @param timelineDataLoader {@link TimelineDataLoader} used to fetch timeline data events.
   * @param tweetFormatUtils {@link TweetFormatUtils} used to format tweet messages.
   * @param twitterApi {@link Twitter} client API used to publish tweets.
   */
  TwitterTimelineEventScheduler(
      final DateUtils dateUtils,
      final MeterRegistry meterRegistry,
      final TimelineDataLoader timelineDataLoader,
      final TweetFormatUtils tweetFormatUtils,
      final Twitter twitterApi) {
    super(dateUtils, tweetFormatUtils, meterRegistry, timelineDataLoader);
    this.twitterApi = twitterApi;
  }

  @Override
  public TimelineEventsPublisherType getPublisherType() {
    return TimelineEventsPublisherType.TWITTER;
  }

  @Scheduled(cron = "0 30 5 * * *")
  @Override
  public void publishTimelineEvent() {
    getMeterRegistry()
        .timer(PUBLISH_TIMER_NAME, TIMELINE_EVENTS_PUBLISHER_TYPE, getPublisherType().name())
        .record(
            () -> {
              publishStatusUpdates();
              log.debug("Execution of scheduled publish of timeline tweets completed.");
            });
  }

  @VisibleForTesting
  protected void publishStatusUpdates() {
    log.debug("Executing scheduled publish of timeline tweets...");
    getMeterRegistry()
        .counter(PUBLISH_EXECUTIONS, TIMELINE_EVENTS_PUBLISHER_TYPE, getPublisherType().name())
        .count();

    final List<Tweet> tweets = generateTimelineEvents();

    if (!CollectionUtils.isEmpty(tweets)) {
      Flux.fromStream(tweets.stream())
          .doOnError(this::handleError)
          .map(this::publishTweet)
          .blockLast();
    } else {
      log.debug("Did not find any timeline events for date '{}'.", getDateUtils().today());
    }
  }

  @VisibleForTesting
  protected Optional<Status> publishTweet(final Tweet tweet) {
    final StatusUpdate mainStatusUpdate = tweet.getMainMessage();

    // Publish the main tweet first
    final Optional<Status> status = publishStatusUpdate(mainStatusUpdate);

    // If successful, reply to the main tweet with the overflow.
    if (status.isPresent()) {
      final List<StatusUpdate> replies = tweet.getReplies(status.get().getId());
      return CollectionUtils.isEmpty(replies)
          ? status
          : Flux.fromIterable(replies).map(this::publishStatusUpdate).blockLast();
    } else {
      return status;
    }
  }

  @VisibleForTesting
  protected Optional<Status> publishStatusUpdate(final StatusUpdate statusUpdate) {
    Status status = null;

    try {
      log.debug("Tweeting event '{}'...", statusUpdate.status);
      status = twitterApi.v1().tweets().updateStatus(statusUpdate);
      log.debug("API returned status for tweet ID {}.", status.getId());
      getMeterRegistry()
          .counter(
              TIMELINE_EVENTS_PUBLISHED, TIMELINE_EVENTS_PUBLISHER_TYPE, getPublisherType().name())
          .count();
    } catch (final TwitterException e) {
      log.error("Unable to publish tweet {}.", statusUpdate, e);
      NewRelic.noticeError(
          e, ImmutableMap.of("today", getDateUtils().today(), "status", statusUpdate.status));
      getMeterRegistry()
          .counter(
              TIMELINE_EVENTS_PUBLISHED_FAILURES,
              TIMELINE_EVENTS_PUBLISHER_TYPE,
              getPublisherType().name())
          .count();
    }

    return Optional.ofNullable(status);
  }

  /** Builds a {@link TwitterTimelineEventScheduler} from the provided data. */
  @SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
  public static class Builder {

    private DateUtils dateUtils;

    private MeterRegistry meterRegistry;

    private TweetFormatUtils tweetFormatUtils;

    private TimelineDataLoader timelineDataLoader;

    private Twitter twitter;

    public Builder withDateUtils(final DateUtils dateUtils) {
      this.dateUtils = dateUtils;
      return this;
    }

    public Builder withMeterRegistry(final MeterRegistry meterRegistry) {
      this.meterRegistry = meterRegistry;
      return this;
    }

    public Builder withTweetFormatUtils(final TweetFormatUtils tweetFormatUtils) {
      this.tweetFormatUtils = tweetFormatUtils;
      return this;
    }

    public Builder withTimelineDataLoader(final TimelineDataLoader timelineDataLoader) {
      this.timelineDataLoader = timelineDataLoader;
      return this;
    }

    public Builder withTwitter(final Twitter twitter) {
      this.twitter = twitter;
      return this;
    }

    /**
     * Builds a {@link TwitterTimelineEventScheduler} from the provided data.
     *
     * @return a {@link TwitterTimelineEventScheduler} instance.
     */
    public TwitterTimelineEventScheduler build() {
      return new TwitterTimelineEventScheduler(
          dateUtils, meterRegistry, timelineDataLoader, tweetFormatUtils, twitter);
    }
  }
}
