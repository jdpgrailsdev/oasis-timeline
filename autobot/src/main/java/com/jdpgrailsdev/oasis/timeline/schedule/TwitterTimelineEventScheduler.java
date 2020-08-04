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
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.Generated;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.newrelic.api.agent.NewRelic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Flux;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterTimelineEventScheduler {

    private static final Logger log = LoggerFactory.getLogger(TwitterTimelineEventScheduler.class);

    private static final String PUBLISH_EXECUTION_COUNTER_NAME = "scheduledTimelineTweetPublish";

    private static final String PUBLISH_TIMER_NAME = "publishTimelineTweet";

    private static final String TIMELINE_EVENTS_PUBLISHED_COUNTER_NAME = "timelineEventsPublished";

    private static final String TIMELINE_EVENTS_PUBLISHED_FAILURE_COUNTER_NAME = "timelineEventsPublishedFailures";

    private final DateUtils dateUtils;

    private final MeterRegistry meterRegistry;

    private final TweetFormatUtils tweetFormatUtils;

    private final TimelineDataLoader timelineDataLoader;

    private final Twitter twitterApi;

    TwitterTimelineEventScheduler(final DateUtils dateUtils, final MeterRegistry meterRegistry, final TimelineDataLoader timelineDataLoader, final TweetFormatUtils tweetFormatUtils, final Twitter twitterApi) {
        this.dateUtils = dateUtils;
        this.meterRegistry = meterRegistry;
        this.tweetFormatUtils = tweetFormatUtils;
        this.timelineDataLoader = timelineDataLoader;
        this.twitterApi = twitterApi;
    }

    @Scheduled(cron = "0 30 5 * * *")
    public void publishTimelineTweet() {
        meterRegistry.timer(PUBLISH_TIMER_NAME).record(() -> {
            publishStatusUpdates();
            log.debug("Execution of scheduled publish of timeline tweets completed.");
        });
    }

    @VisibleForTesting
    protected void publishStatusUpdates() {
        log.debug("Executing scheduled publish of timeline tweets...");
        meterRegistry.counter(PUBLISH_EXECUTION_COUNTER_NAME).count();

        final List<Tweet> tweets = generateTimelineEventsTweets();

        if(!CollectionUtils.isEmpty(tweets)) {
            Flux.fromStream(tweets.stream())
                .doOnError(this::handleError)
                .map(this::publishTweet)
                .blockLast();
        } else {
            log.debug("Did not find any timeline events for date '{}'.", dateUtils.today());
        }
    }

    private List<Tweet> generateTimelineEventsTweets() {
        final String today = dateUtils.today();
        log.debug("Fetching timeline events for today's date {}...", today);
        return timelineDataLoader.getHistory(today).stream()
            .map(this::convertEventToTweet)
            .filter(t -> t != null)
            .collect(Collectors.toList());
    }

    private Tweet convertEventToTweet(final TimelineData timelineData) {
        try {
            return tweetFormatUtils.generateTweet(timelineData, timelineDataLoader.getAdditionalHistoryContext(timelineData));
        } catch(final TwitterException e) {
            log.error("Unable to generate tweet for timeline data {}.", timelineData, e);
            NewRelic.noticeError(e, ImmutableMap.of("timeline_title", timelineData.getTitle(),
                    "timeline_description", timelineData.getDescription(), "timeline_date", timelineData.getDate(),
                    "timeline_type", timelineData.getType(), "timeline_year", timelineData.getYear()));
            return null;
        }
    }

    private Optional<Status> publishTweet(final Tweet tweet) {
        final StatusUpdate mainStatusUpdate = tweet.getMainTweet();

        // Publish the main tweet first
        final Optional<Status> status = publishStatusUpdate(mainStatusUpdate);

        // If successful, reply to the main tweet with the overflow.
        if(status.isPresent()) {
            final List<StatusUpdate> replies = tweet.getReplies(status.get().getId());
            return CollectionUtils.isEmpty(replies) ? status :
                Flux.fromIterable(replies)
                    .map(this::publishStatusUpdate)
                    .blockLast();
        } else {
            return status;
        }
    }

    private Optional<Status> publishStatusUpdate(final StatusUpdate statusUpdate) {
        Status status = null;

        try {
            log.debug("Tweeting event '{}'...", statusUpdate.getStatus());
            status = twitterApi.updateStatus(statusUpdate);
            log.debug("API returned status for tweet ID {}.", status.getId());
            meterRegistry.counter(TIMELINE_EVENTS_PUBLISHED_COUNTER_NAME).count();
        } catch (final TwitterException e) {
            log.error("Unable to publish tweet {}.", statusUpdate.toString());
            NewRelic.noticeError(e, ImmutableMap.of("today", dateUtils.today(), "status", statusUpdate.getStatus()));
            meterRegistry.counter(TIMELINE_EVENTS_PUBLISHED_FAILURE_COUNTER_NAME).count();
        }

        return Optional.ofNullable(status);
    }

    @Generated
    private void handleError(final Throwable t) {
        log.error("Unable to publish status updates.", t);
        NewRelic.noticeError(t);
    }

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

        public TwitterTimelineEventScheduler build() {
            return new TwitterTimelineEventScheduler(dateUtils, meterRegistry, timelineDataLoader, tweetFormatUtils, twitter);
        }
    }
}