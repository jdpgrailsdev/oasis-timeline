package com.jdpgrailsdev.oasis.timeline.schedule;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;

class TwitterTimelineEventSchedulerBuilderTests {

  @Test
  void testBuilder() {
    final DateUtils dateUtils = mock(DateUtils.class);
    final MeterRegistry meterRegistry = mock(MeterRegistry.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TweetFormatUtils tweetFormatUtils = mock(TweetFormatUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitterCredentials(twitterCredentials)
            .build();

    assertNotNull(scheduler, "Scheduler should not be null");
    assertNotNull(scheduler.getTwitterApi(), "TwitterApi should not be null");
  }
}
