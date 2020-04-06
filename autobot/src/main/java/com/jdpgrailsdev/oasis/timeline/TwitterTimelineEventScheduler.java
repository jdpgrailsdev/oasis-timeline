package com.jdpgrailsdev.oasis.timeline;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.newrelic.api.agent.NewRelic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MeterRegistry;
import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterTimelineEventScheduler {

    private static final Logger log = LoggerFactory.getLogger(TwitterTimelineEventScheduler.class);

    private static final GeoLocation LOCATION = new GeoLocation(53.422201, -2.208914);

    private static final String PUBLISH_EXECUTION_COUNTER_NAME = "scheduledTimelineTweetPublish";

    private static final String PUBLISH_TIMER_NAME = "publishTimelineTweet";

    private static final String TIMELINE_EVENTS_PUBLISHED_COUNTER_NAME = "timelineEventsPublished";

    private static final String TIMELINE_EVENTS_PUBLISHED_FAILURE_COUNTER_NAME = "timelineEventsPublishedFailures";

    private final DateUtils dateUtils;

    private final MeterRegistry meterRegistry;

    private final ITemplateEngine textTemplateEngine;

    private final TimelineDataLoader timelineDataLoader;

    private final Twitter twitterApi;

    TwitterTimelineEventScheduler(final DateUtils dateUtils, final MeterRegistry meterRegistry, final ITemplateEngine textTemplateEngine, final TimelineDataLoader timelineDataLoader, final Twitter twitterApi) {
        this.dateUtils = dateUtils;
        this.meterRegistry = meterRegistry;
        this.textTemplateEngine = textTemplateEngine;
        this.timelineDataLoader = timelineDataLoader;
        this.twitterApi = twitterApi;
    }

    @Scheduled(cron = "0 0 1 * * ?")
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

        final List<StatusUpdate> latestStatuses = generateTimelineEventsStatus();

        if(!CollectionUtils.isEmpty(latestStatuses)) {
            latestStatuses.forEach(this::publishStatusUpdate);
        } else {
            log.debug("Did not find any timeline events for date '{}'.", dateUtils.today());
        }
    }

    private List<StatusUpdate> generateTimelineEventsStatus() {
        final Instant today = dateUtils.today();
        log.debug("Fetching timeline events for today's date {}...", today);
        return timelineDataLoader.getHistory(today).stream()
            .map(this::convertEventToStatusUpdate)
            .collect(Collectors.toList());
    }

    private StatusUpdate convertEventToStatusUpdate(final TimelineData event) {
        final String text = generateTimelineEventsText(event);
        log.debug("Generated '{}' from timeline event '{}'.", text, event);
        final StatusUpdate update = new StatusUpdate(text);
        update.setLocation(LOCATION);
        return update;
    }

    private String generateTimelineEventsText(final TimelineData timelineData) {
        final Context context = new Context();
        context.setVariable("description", timelineData.getDescription());
        context.setVariable("year", timelineData.getYear());
        return textTemplateEngine.process("tweet", context);
    }

    private void publishStatusUpdate(final StatusUpdate latestStatus) {
        try {
            log.debug("Tweeting event '{}'...", latestStatus.getStatus());
            twitterApi.updateStatus(latestStatus);
            meterRegistry.counter(TIMELINE_EVENTS_PUBLISHED_COUNTER_NAME).count();
        } catch (final TwitterException e) {
            log.error("Unable to publish tweet {}.", latestStatus.toString());
            NewRelic.noticeError(e, ImmutableMap.of("today", dateUtils.today(), "status", latestStatus.getStatus()));
            meterRegistry.counter(TIMELINE_EVENTS_PUBLISHED_FAILURE_COUNTER_NAME).count();
        }
    }

    public static class Builder {

        private DateUtils dateUtils;

        private MeterRegistry meterRegistry;

        private ITemplateEngine templateEngine;

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

        public Builder withTemplateEngine(final ITemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
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
            return new TwitterTimelineEventScheduler(dateUtils, meterRegistry, templateEngine, timelineDataLoader, twitter);
        }
    }
}
