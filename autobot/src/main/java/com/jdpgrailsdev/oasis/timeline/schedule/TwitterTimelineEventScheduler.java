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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.exception.TweetException;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.Generated;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.newrelic.api.agent.NewRelic;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

/** Spring scheduler that publishes tweets for daily events on a fixed schedule. */
public class TwitterTimelineEventScheduler {

  private static final Logger log = LoggerFactory.getLogger(TwitterTimelineEventScheduler.class);

  public static final String PUBLISH_EXECUTIONS = "scheduledTimelineTweetPublish";

  public static final String PUBLISH_TIMER_NAME = "publishTimelineTweet";

  public static final String TIMELINE_EVENTS_PUBLISHED = "timelineEventsPublished";

  public static final String TIMELINE_EVENTS_PUBLISHED_FAILURES = "timelineEventsPublishedFailures";

  public static final String TOKEN_REFRESH_COUNTER_NAME = "oauth2TokenRefresh";

  private final DateUtils dateUtils;

  private final MeterRegistry meterRegistry;

  private final TweetFormatUtils tweetFormatUtils;

  private final TimelineDataLoader timelineDataLoader;

  private final TwitterApiUtils twitterApiUtils;

  /**
   * Constructs a new scheduler.
   *
   * @param dateUtils {@link DateUtils} used to format date strings.
   * @param meterRegistry {@link MeterRegistry} used to record metrics.
   * @param timelineDataLoader {@link TimelineDataLoader} used to fetch timeline data events.
   * @param tweetFormatUtils {@link TweetFormatUtils} used to format tweet messages.
   * @param twitterApiUtils {@link TwitterApiUtils} used to create API clients.
   */
  TwitterTimelineEventScheduler(
      final DateUtils dateUtils,
      final MeterRegistry meterRegistry,
      final TimelineDataLoader timelineDataLoader,
      final TweetFormatUtils tweetFormatUtils,
      final TwitterApiUtils twitterApiUtils) {
    this.dateUtils = dateUtils;
    this.meterRegistry = meterRegistry;
    this.tweetFormatUtils = tweetFormatUtils;
    this.timelineDataLoader = timelineDataLoader;
    this.twitterApiUtils = twitterApiUtils;
  }

  /** Publishes tweets for each timeline event associated with today's date. */
  @Scheduled(cron = "0 30 5 * * *")
  public void publishTimelineTweet() {
    meterRegistry
        .timer(PUBLISH_TIMER_NAME)
        .record(
            () -> {
              publishStatusUpdates();
              log.debug("Execution of scheduled publish of timeline tweets completed.");
            });
  }

  @VisibleForTesting
  protected void publishStatusUpdates() {
    log.debug("Executing scheduled publish of timeline tweets...");
    meterRegistry.counter(PUBLISH_EXECUTIONS).count();

    final List<Tweet> tweets = generateTimelineEventsTweets();

    if (!CollectionUtils.isEmpty(tweets)) {
      Flux.fromStream(tweets.stream())
          .doOnError(this::handleError)
          .map(this::publishTweet)
          .blockLast();
    } else {
      log.debug("Did not find any timeline events for date '{}'.", dateUtils.today());
    }
  }

  @VisibleForTesting
  protected List<Tweet> generateTimelineEventsTweets() {
    final String today = dateUtils.today();
    log.debug("Fetching timeline events for today's date {}...", today);
    return timelineDataLoader.getHistory(today).stream()
        .map(this::convertEventToTweet)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @VisibleForTesting
  protected Tweet convertEventToTweet(final TimelineData timelineData) {
    try {
      return tweetFormatUtils.generateTweet(
          timelineData, timelineDataLoader.getAdditionalHistoryContext(timelineData));
    } catch (final TweetException e) {
      log.error("Unable to generate tweet for timeline data {}.", timelineData, e);
      NewRelic.noticeError(
          e,
          ImmutableMap.of(
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
      return null;
    }
  }

  @VisibleForTesting
  protected Optional<TweetCreateResponse> publishTweet(final Tweet tweet) {
    final TweetCreateRequest tweetCreateRequest = tweet.getMainTweet();

    // Publish the main tweet first
    final Optional<TweetCreateResponse> response = publishTweet(tweetCreateRequest);

    // If successful, reply to the main tweet with the overflow.
    if (response.isPresent()) {
      final List<TweetCreateRequest> replies = tweet.getReplies(response.get().getData().getId());
      return CollectionUtils.isEmpty(replies)
          ? response
          : Flux.fromIterable(replies).map(this::publishTweet).blockLast();
    } else {
      return response;
    }
  }

  @VisibleForTesting
  protected Optional<TweetCreateResponse> publishTweet(
      final TweetCreateRequest tweetCreateRequest) {
    TweetCreateResponse tweetResponse = null;

    try {
      log.debug("Tweeting event '{}'...", tweetCreateRequest.getText());
      tweetResponse =
          twitterApiUtils.getTwitterApi().tweets().createTweet(tweetCreateRequest).execute();
      log.debug("API returned status for tweet ID {}.", tweetResponse.getData().getId());
      meterRegistry.counter(TIMELINE_EVENTS_PUBLISHED).count();
    } catch (final ApiException e) {
      log.error("Unable to publish tweet {}.", tweetCreateRequest, e);
      NewRelic.noticeError(
          e, ImmutableMap.of("today", dateUtils.today(), "tweet", tweetCreateRequest.getText()));
      meterRegistry.counter(TIMELINE_EVENTS_PUBLISHED_FAILURES).count();
    }

    return Optional.ofNullable(tweetResponse);
  }

  @Generated
  private void handleError(final Throwable throwable) {
    log.error("Unable to publish status updates.", throwable);
    NewRelic.noticeError(throwable);
  }

  /** Builds a {@link TwitterTimelineEventScheduler} from the provided data. */
  @SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
  public static class Builder {

    private DateUtils dateUtils;

    private MeterRegistry meterRegistry;

    private TweetFormatUtils tweetFormatUtils;

    private TimelineDataLoader timelineDataLoader;

    private TwitterApiUtils twitterApiUtils;

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

    public Builder withTwitterApiUtils(final TwitterApiUtils twitterApiUtils) {
      this.twitterApiUtils = twitterApiUtils;
      return this;
    }

    /**
     * Builds a {@link TwitterTimelineEventScheduler} from the provided data.
     *
     * @return a {@link TwitterTimelineEventScheduler} instance.
     */
    public TwitterTimelineEventScheduler build() {
      return new TwitterTimelineEventScheduler(
          dateUtils, meterRegistry, timelineDataLoader, tweetFormatUtils, twitterApiUtils);
    }
  }
}
