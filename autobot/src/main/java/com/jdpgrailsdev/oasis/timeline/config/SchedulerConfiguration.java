package com.jdpgrailsdev.oasis.timeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.Oauth2Scheduler;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Spring configuration for scheduler beans. */
@Configuration
@EnableScheduling
public class SchedulerConfiguration {

  /**
   * Defines the {@link DateUtils} bean.
   *
   * @return The {@link DateUtils} bean.
   */
  @Bean
  public DateUtils dateUtils() {
    return new DateUtils();
  }

  /**
   * Defines the {@link TwitterTimelineEventScheduler} bean.
   *
   * @param dateUtils {@link DateUtils} bean.
   * @param meterRegistry Micrometer {@link MeterRegistry} bean.
   * @param timelineDataLoader The {@link TimelineDataLoader} bean.
   * @param tweetFormatUtils The {@link TweetFormatUtils} bean.
   * @param twitterApiUtils The {@link TwitterApiUtils} bean.
   * @return The {@link TwitterTimelineEventScheduler} bean.
   */
  @Bean
  public TwitterTimelineEventScheduler twitterTimelineEventScheduler(
      final DateUtils dateUtils,
      final MeterRegistry meterRegistry,
      final TimelineDataLoader timelineDataLoader,
      final TweetFormatUtils tweetFormatUtils,
      final TwitterApiUtils twitterApiUtils) {
    return new TwitterTimelineEventScheduler.Builder()
        .withDateUtils(dateUtils)
        .withMeterRegistry(meterRegistry)
        .withTimelineDataLoader(timelineDataLoader)
        .withTweetFormatUtils(tweetFormatUtils)
        .withTwitterApiUtils(twitterApiUtils)
        .build();
  }

  /**
   * Defines the {@link Oauth2Scheduler} bean.
   *
   * @param meterRegistry Micrometer {@link MeterRegistry} bean.
   * @param twitterApiUtils The {@link TwitterApiUtils} bean.
   * @return The {@link Oauth2Scheduler} bean.
   */
  @Bean
  public Oauth2Scheduler oauth2Scheduler(
      final MeterRegistry meterRegistry, final TwitterApiUtils twitterApiUtils) {
    return new Oauth2Scheduler(meterRegistry, twitterApiUtils);
  }

  /**
   * Defines the {@link ResourcePatternResolver} bean used to find and load the data file.
   *
   * @return The {@link ResourcePatternResolver} bean.
   */
  @Bean
  public ResourcePatternResolver timelineDataFileResourceResolver() {
    return new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
  }

  /**
   * Defines the {@link TimelineDataLoader} bean.
   *
   * @param objectMapper A Jackson {@link ObjectMapper} instance.
   * @param timelineDataFileResourceResolver The data file resource resolver bean.
   * @return The {@link TimelineDataLoader} bean.
   */
  @Bean
  public TimelineDataLoader timelineDataLoader(
      final ObjectMapper objectMapper,
      final ResourcePatternResolver timelineDataFileResourceResolver) {
    return new TimelineDataLoader(objectMapper, timelineDataFileResourceResolver);
  }
}
