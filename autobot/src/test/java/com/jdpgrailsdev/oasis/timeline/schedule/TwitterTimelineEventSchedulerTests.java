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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import com.jdpgrailsdev.oasis.timeline.config.TweetContext;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@SuppressWarnings("PMD.SingularField")
class TwitterTimelineEventSchedulerTests {

  private DateUtils dateUtils;

  private MeterRegistry meterRegistry;

  private TweetFormatUtils tweetFormatUtils;

  private Twitter twitterApi;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() throws TwitterException {
    final ITemplateEngine templateEngine = mock(ITemplateEngine.class);
    final TimelineDataLoader timelineDataLoader = mock(TimelineDataLoader.class);
    final Timer timer = mock(Timer.class);
    final TweetContext tweetContext = mock(TweetContext.class);
    final Status tweetStatus = mock(Status.class);
    dateUtils = mock(DateUtils.class);
    meterRegistry = mock(MeterRegistry.class);
    tweetFormatUtils = new TweetFormatUtils(templateEngine, tweetContext);
    twitterApi = mock(Twitter.class);

    objectMapper =
        JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    when(dateUtils.today()).thenReturn("January 1");
    when(meterRegistry.counter(anyString())).thenReturn(mock(Counter.class));
    when(meterRegistry.timer(anyString())).thenReturn(timer);
    when(templateEngine.process(anyString(), any(IContext.class)))
        .thenReturn("This is a template string.");
    when(timer.record(any(Supplier.class)))
        .thenAnswer(
            invocation -> {
              final Supplier supplier = invocation.getArgument(0);
              return supplier.get();
            });
    when(timelineDataLoader.getHistory(anyString())).thenReturn(List.of());
    when(tweetContext.getHashtags()).thenReturn(Set.of("hashtag1", "hashtag2"));
    when(tweetContext.getMentions()).thenReturn(Map.of());
    when(tweetContext.getUncapitalizeExclusions()).thenReturn(Set.of("Proper Noun"));
    when(tweetStatus.getId()).thenReturn(12345L);
    when(twitterApi.updateStatus(any(StatusUpdate.class))).thenReturn(tweetStatus);
  }

  @Test
  @DisplayName(
      "test that when the scheduled task runs, tweets are published for each timeline event")
  void testScheduledTask() throws IOException, TwitterException {
    final Resource additionalTimelineDataResource =
        new ClassPathResource(
            "/json/additionalContextData.json", Thread.currentThread().getContextClassLoader());
    final Resource timelineDataResource =
        new ClassPathResource(
            "/json/testTimelineData.json", Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {additionalTimelineDataResource});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});

    loader.afterPropertiesSet();

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build();

    scheduler.publishStatusUpdates();

    verify(meterRegistry, times(1)).counter(TwitterTimelineEventScheduler.PUBLISH_EXECUTIONS);
    verify(meterRegistry, times(4))
        .counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED);
    verify(twitterApi, times(4)).updateStatus(any(StatusUpdate.class));
  }

  @Test
  @DisplayName("test that when no events could be found, nothing is published")
  void testPublishingStatusUpdatesNoEventsFound() throws TwitterException {
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);

    when(loader.getHistory(anyString())).thenReturn(List.of());

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build();

    scheduler.publishStatusUpdates();

    verify(twitterApi, times(0)).updateStatus(any(StatusUpdate.class));
  }

  @Test
  @DisplayName("test that null events are filtered prior to publishing")
  void testHandlingNullEventsDuringPublishingStatus() throws TwitterException {
    tweetFormatUtils = mock(TweetFormatUtils.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TimelineData timelineData = mock(TimelineData.class);

    when(timelineData.getDate()).thenReturn("date");
    when(timelineData.getDescription()).thenReturn("description");
    when(timelineData.getTitle()).thenReturn("title");
    when(timelineData.getType()).thenReturn(TimelineDataType.GIGS);
    when(timelineData.getYear()).thenReturn(2020);
    when(loader.getHistory(anyString())).thenReturn(List.of(timelineData));
    when(tweetFormatUtils.generateTweet(any(TimelineData.class), anyList()))
        .thenThrow(TwitterException.class);

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build();

    scheduler.publishStatusUpdates();

    verify(twitterApi, times(0)).updateStatus(any(StatusUpdate.class));
  }

  @Test
  @DisplayName("test that when the scheduled task run a timer metric is recorded")
  void testPublishTweetMetricRecorded() throws IOException {
    final Resource additionalTimelineDataResource =
        new ClassPathResource(
            "/json/additionalContextData.json", Thread.currentThread().getContextClassLoader());
    final Resource timelineDataResource =
        new ClassPathResource(
            "/json/testTimelineData.json", Thread.currentThread().getContextClassLoader());
    final ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
    final TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver);

    when(resolver.getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {additionalTimelineDataResource});
    when(resolver.getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION))
        .thenReturn(new Resource[] {timelineDataResource});

    loader.afterPropertiesSet();

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build();

    scheduler.publishTimelineTweet();

    verify(meterRegistry, times(1)).timer(TwitterTimelineEventScheduler.PUBLISH_TIMER_NAME);
  }

  @Test
  @DisplayName("test that when conversion of an event to a Tweet fails, the exception is handled")
  void testConvertEventToTweetExceptionHandling() throws TwitterException {
    tweetFormatUtils = mock(TweetFormatUtils.class);

    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TimelineData timelineData = mock(TimelineData.class);

    when(loader.getAdditionalHistoryContext(any(TimelineData.class))).thenReturn(List.of());
    when(timelineData.getDate()).thenReturn("date");
    when(timelineData.getDescription()).thenReturn("description");
    when(timelineData.getTitle()).thenReturn("title");
    when(timelineData.getType()).thenReturn(TimelineDataType.GIGS);
    when(timelineData.getYear()).thenReturn(2020);
    when(tweetFormatUtils.generateTweet(any(TimelineData.class), anyList()))
        .thenThrow(TwitterException.class);

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build();

    final Tweet tweet = scheduler.convertEventToTweet(timelineData);

    assertNull(tweet, AssertionMessage.NULL.toString());
  }

  @Test
  @DisplayName("test that when the publishing of status updates fails, the exception is handled")
  void testPublishStatusUpdateExceptionHandling() throws TwitterException {
    twitterApi = mock(Twitter.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final StatusUpdate statusUpdate = new StatusUpdate("status");

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build();

    when(twitterApi.updateStatus(any(StatusUpdate.class))).thenThrow(TwitterException.class);

    final Optional<Status> result = scheduler.publishStatusUpdate(statusUpdate);

    verify(meterRegistry, times(1))
        .counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED_FAILURES);
    assertTrue(result.isEmpty(), AssertionMessage.VALUE.toString());
  }

  @Test
  @DisplayName(
      "test that when a tweet without any replies is published, the single status is returned")
  void testPublishingTweetWithoutReplies() throws TwitterException {
    twitterApi = mock(Twitter.class);
    final Tweet tweet = mock(Tweet.class);
    final Status status = mock(Status.class);
    final StatusUpdate statusUpdate = new StatusUpdate("status");
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);

    when(tweet.getMainTweet()).thenReturn(statusUpdate);
    when(tweet.getReplies(anyLong())).thenReturn(List.of(statusUpdate));
    when(twitterApi.updateStatus(any(StatusUpdate.class))).thenReturn(status);

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build();

    final Optional<Status> result = scheduler.publishTweet(tweet);
    assertEquals(status, result.orElse(null), AssertionMessage.VALUE.toString());
  }

  @Test
  @DisplayName("test that when publishing a tweet fails, a null status is returned")
  void testPublishingTweetWithFailure() throws TwitterException {
    twitterApi = mock(Twitter.class);
    final Tweet tweet = mock(Tweet.class);
    final StatusUpdate statusUpdate = new StatusUpdate("status");
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);

    when(tweet.getMainTweet()).thenReturn(statusUpdate);
    when(tweet.getReplies(anyLong())).thenReturn(List.of(statusUpdate));
    when(twitterApi.updateStatus(any(StatusUpdate.class))).thenThrow(TwitterException.class);

    final TwitterTimelineEventScheduler scheduler =
        new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(loader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build();

    final Optional<Status> result = scheduler.publishTweet(tweet);
    assertTrue(result.isEmpty(), AssertionMessage.VALUE.toString());
  }
}
