package com.jdpgrailsdev.oasis.timeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.Oauth2Scheduler;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
import dev.failsafe.RetryPolicy;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
   * @param rateLimitRetries The number of retries to attempt when encountering rate limiting.
   * @param timelineDataLoader The {@link TimelineDataLoader} bean.
   * @param tweetFormatUtils The {@link TweetFormatUtils} bean.
   * @param twitterApiUtils The {@link TwitterApiUtils} bean.
   * @param tweetPostRetryPolicy The {@link RetryPolicy} for tweets bean.
   * @return The {@link TwitterTimelineEventScheduler} bean.
   */
  @Bean
  public TwitterTimelineEventScheduler twitterTimelineEventScheduler(
      final DateUtils dateUtils,
      final MeterRegistry meterRegistry,
      @Value("${twitter.tweet.rate-limit-retries:3}") final Integer rateLimitRetries,
      final TimelineDataLoader timelineDataLoader,
      final TweetFormatUtils tweetFormatUtils,
      final TwitterApiUtils twitterApiUtils,
      @Qualifier("tweetPostRetryPolicy") final RetryPolicy<Object> tweetPostRetryPolicy) {
    return new TwitterTimelineEventScheduler.Builder()
        .withDateUtils(dateUtils)
        .withMeterRegistry(meterRegistry)
        .withRateLimitRetries(rateLimitRetries)
        .withTimelineDataLoader(timelineDataLoader)
        .withTweetFormatUtils(tweetFormatUtils)
        .withTwitterApiUtils(twitterApiUtils)
        .withTweetRetryPolicy(tweetPostRetryPolicy)
        .build();
  }

  /**
   * Defines the {@link Oauth2Scheduler} bean.
   *
   * @param authRefreshRetryPolicy {@link RetryPolicy} for authentication token refresh bean.
   * @param meterRegistry Micrometer {@link MeterRegistry} bean.
   * @param twitterApiUtils The {@link TwitterApiUtils} bean.
   * @return The {@link Oauth2Scheduler} bean.
   */
  @Bean
  public Oauth2Scheduler oauth2Scheduler(
      @Qualifier("authRefreshRetryPolicy") final RetryPolicy<Object> authRefreshRetryPolicy,
      final MeterRegistry meterRegistry,
      final TwitterApiUtils twitterApiUtils) {
    return new Oauth2Scheduler(authRefreshRetryPolicy, meterRegistry, twitterApiUtils);
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

  /**
   * Failsafe {@link RetryPolicy} used to ensure that the refresh token can be successfully
   * obtained.
   *
   * @param delayMs The delay in milliseconds to wait between retries.
   * @param retries The number of retries to attempt before giving up.
   * @return The {@link RetryPolicy} to be used to refresh authorization tokens.
   */
  @Bean
  public RetryPolicy<Object> authRefreshRetryPolicy(
      @Value("${twitter.oauth.refresh.retry-delay-ms:30000}") final Long delayMs,
      @Value("${twitter.oauth.refresh.retries:3}") final Integer retries) {
    return RetryPolicy.builder()
        .handle(ApiException.class)
        .handleResult(null)
        .withDelay(Duration.ofMillis(delayMs))
        .withMaxRetries(retries)
        .build();
  }

  /**
   * Failsafe {@link RetryPolicy} used to ensure that a tweet post can be successfully made.
   *
   * @param delayMs The delay in milliseconds to wait between retries.
   * @param retries The number of retries to attempt before giving up.
   * @return The {@link RetryPolicy} to be used to refresh authorization tokens.
   */
  @Bean
  public RetryPolicy<Object> tweetPostRetryPolicy(
      @Value("${twitter.tweet.retry-delay-ms:30000}") final Long delayMs,
      @Value("${twitter.tweet.retries:3}") final Integer retries) {
    return RetryPolicy.builder()
        .handle(ApiException.class)
        .withDelay(Duration.ofMillis(delayMs))
        .withMaxRetries(retries)
        .build();
  }
}
