package com.jdpgrailsdev.oasis.timeline.schedule;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import dev.failsafe.RetryPolicy;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;

class TwitterTimelineEventSchedulerBuilderTests {

  @Test
  void testBuilder() {
    final DateUtils dateUtils = mock(DateUtils.class);
    final MeterRegistry meterRegistry = mock(MeterRegistry.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TweetFormatUtils tweetFormatUtils = mock(TweetFormatUtils.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final RetryPolicy<Object> retryPolicy = mock(RetryPolicy.class);
    final Integer rateLimitRetries = 3;

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withRateLimitRetries(rateLimitRetries)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitterApiUtils(twitterApiUtils)
            .withTweetRetryPolicy(retryPolicy)
            .build();

    assertNotNull(scheduler, "Scheduler should not be null");
  }
}
