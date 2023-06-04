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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import com.jdpgrailsdev.oasis.timeline.config.TweetContext;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetException;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TweetsApi.APIcreateTweetRequest;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateResponse;
import com.twitter.clientlib.model.TweetCreateResponseData;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
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

@SuppressWarnings("PMD.SingularField")
class TwitterTimelineEventSchedulerTests {

  private APIcreateTweetRequest apiCreateTweetRequest;
  private DateUtils dateUtils;
  private MeterRegistry meterRegistry;
  private TweetCreateResponse response;
  private TweetCreateResponseData responseData;
  private TweetFormatUtils tweetFormatUtils;
  private TweetsApi tweetsApi;
  private TwitterApi twitterApi;
  private TwitterCredentialsOAuth2 twitterCredentials;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() throws ApiException {
    final ITemplateEngine templateEngine = mock(ITemplateEngine.class);
    final TimelineDataLoader timelineDataLoader = mock(TimelineDataLoader.class);
    final Timer timer = mock(Timer.class);
    final TweetContext tweetContext = mock(TweetContext.class);
    dateUtils = mock(DateUtils.class);
    meterRegistry = mock(MeterRegistry.class);
    tweetFormatUtils = new TweetFormatUtils(templateEngine, tweetContext);
    twitterApi = mock(TwitterApi.class);
    tweetsApi = mock(TweetsApi.class);
    twitterCredentials = mock(TwitterCredentialsOAuth2.class);
    apiCreateTweetRequest = mock(APIcreateTweetRequest.class);
    response = mock(TweetCreateResponse.class);
    responseData = mock(TweetCreateResponseData.class);

    objectMapper =
        JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    when(dateUtils.today()).thenReturn("January 1");
    when(meterRegistry.counter(anyString())).thenReturn(mock(Counter.class));
    when(meterRegistry.counter(anyString(), any(Iterable.class))).thenReturn(mock(Counter.class));
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
    when(responseData.getId()).thenReturn("12345");
    when(response.getData()).thenReturn(responseData);
    when(apiCreateTweetRequest.execute()).thenReturn(response);
    when(tweetsApi.createTweet(any())).thenReturn(apiCreateTweetRequest);
    when(twitterApi.tweets()).thenReturn(tweetsApi);
  }

  @Test
  @DisplayName(
      "test that when the scheduled task runs, tweets are published for each timeline event")
  void testScheduledTask() throws IOException, ApiException {
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
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    scheduler.publishStatusUpdates();

    verify(meterRegistry, times(1)).counter(TwitterTimelineEventScheduler.PUBLISH_EXECUTIONS);
    verify(meterRegistry, times(4))
        .counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED);
    verify(apiCreateTweetRequest, times(4)).execute();
  }

  @Test
  @DisplayName("test that when no events could be found, nothing is published")
  void testPublishingStatusUpdatesNoEventsFound() throws ApiException {
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);

    when(loader.getHistory(anyString())).thenReturn(List.of());

    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    scheduler.publishStatusUpdates();

    verify(apiCreateTweetRequest, times(0)).execute();
  }

  @Test
  @DisplayName("test that null events are filtered prior to publishing")
  void testHandlingNullEventsDuringPublishingStatus() throws ApiException, TweetException {
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
        .thenThrow(TweetException.class);

    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    scheduler.publishStatusUpdates();

    verify(apiCreateTweetRequest, times(0)).execute();
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
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    scheduler.publishTimelineTweet();

    verify(meterRegistry, times(1)).timer(TwitterTimelineEventScheduler.PUBLISH_TIMER_NAME);
  }

  @Test
  @DisplayName("test that when conversion of an event to a Tweet fails, the exception is handled")
  void testConvertEventToTweetExceptionHandling() throws TweetException {
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
        .thenThrow(TweetException.class);

    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    final Tweet tweet = scheduler.convertEventToTweet(timelineData);

    assertNull(tweet, AssertionMessage.NULL.toString());
  }

  @Test
  @DisplayName("test that when the publishing of status updates fails, the exception is handled")
  void testPublishStatusUpdateExceptionHandling() throws ApiException {
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TweetCreateRequest tweetCreateRequest = new TweetCreateRequest();
    tweetCreateRequest.setText("status");

    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    final APIcreateTweetRequest apiCreateTweetRequest = mock(APIcreateTweetRequest.class);
    final TweetsApi tweetsApi = mock(TweetsApi.class);

    when(apiCreateTweetRequest.execute()).thenThrow(ApiException.class);
    when(tweetsApi.createTweet(any())).thenReturn(apiCreateTweetRequest);
    when(twitterApi.tweets()).thenReturn(tweetsApi);

    final Optional<TweetCreateResponse> result = scheduler.publishTweet(tweetCreateRequest);

    verify(meterRegistry, times(1))
        .counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED_FAILURES);
    assertTrue(result.isEmpty(), AssertionMessage.VALUE.toString());
  }

  @Test
  @DisplayName(
      "test that when a tweet without any replies is published, the single status is returned")
  void testPublishingTweetWithoutReplies() throws ApiException {
    final Tweet tweet = mock(Tweet.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TweetCreateResponse response = mock(TweetCreateResponse.class);
    final TweetCreateResponseData responseData = mock(TweetCreateResponseData.class);
    final TweetCreateRequest tweetCreateRequest = new TweetCreateRequest();
    tweetCreateRequest.setText("status");

    when(tweet.getMainTweet()).thenReturn(tweetCreateRequest);
    when(tweet.getReplies(anyString())).thenReturn(List.of(tweetCreateRequest));
    when(response.getData()).thenReturn(responseData);

    final APIcreateTweetRequest apiCreateTweetRequest = mock(APIcreateTweetRequest.class);
    final TweetsApi tweetsApi = mock(TweetsApi.class);

    when(apiCreateTweetRequest.execute()).thenReturn(response);
    when(tweetsApi.createTweet(any())).thenReturn(apiCreateTweetRequest);
    when(twitterApi.tweets()).thenReturn(tweetsApi);

    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    final Optional<TweetCreateResponse> result = scheduler.publishTweet(tweet);
    assertEquals(response, result.orElse(null), AssertionMessage.VALUE.toString());
  }

  @Test
  @DisplayName("test that when publishing a tweet fails, a null status is returned")
  void testPublishingTweetWithFailure() throws ApiException {
    final Tweet tweet = mock(Tweet.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TweetCreateRequest tweetCreateRequest = new TweetCreateRequest();
    tweetCreateRequest.setText("status");

    when(tweet.getMainTweet()).thenReturn(tweetCreateRequest);
    when(tweet.getReplies(anyString())).thenReturn(List.of(tweetCreateRequest));

    when(apiCreateTweetRequest.execute()).thenThrow(ApiException.class);
    when(tweetsApi.createTweet(any())).thenReturn(apiCreateTweetRequest);
    when(twitterApi.tweets()).thenReturn(tweetsApi);

    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    final Optional<TweetCreateResponse> result = scheduler.publishTweet(tweet);
    assertTrue(result.isEmpty(), AssertionMessage.VALUE.toString());
  }

  @Test
  void testAutomaticAccessTokenRefresh() throws ApiException {
    final String accessToken = "access";
    final String refreshToken = "refresh";
    final OAuth2AccessToken oauthAccessToken = mock(OAuth2AccessToken.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));

    when(oauthAccessToken.getAccessToken()).thenReturn(accessToken);
    when(oauthAccessToken.getRefreshToken()).thenReturn(refreshToken);
    when(twitterApi.refreshToken()).thenReturn(oauthAccessToken);
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    assertDoesNotThrow(() -> scheduler.refreshAccess());
    verify(twitterCredentials, times(1)).setTwitterOauth2AccessToken(accessToken);
    verify(twitterCredentials, times(1)).setTwitterOauth2RefreshToken(refreshToken);
    verify(meterRegistry, times(1))
        .counter(
            TwitterTimelineEventScheduler.TOKEN_REFRESH_COUNTER_NAME,
            Set.of(new ImmutableTag("result", "success")));
  }

  @Test
  void testAutomaticAccessTokenRefreshNull() throws ApiException {
    final String accessToken = "access";
    final String refreshToken = "refresh";
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));

    when(twitterApi.refreshToken()).thenReturn(null);
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    assertDoesNotThrow(() -> scheduler.refreshAccess());
    verify(twitterCredentials, times(0)).setTwitterOauth2AccessToken(accessToken);
    verify(twitterCredentials, times(0)).setTwitterOauth2RefreshToken(refreshToken);
    verify(meterRegistry, times(1))
        .counter(
            TwitterTimelineEventScheduler.TOKEN_REFRESH_COUNTER_NAME,
            Set.of(new ImmutableTag("result", "failure")));
  }

  @Test
  void testAutomaticAccessTokenRefreshFailure() throws ApiException {
    final String accessToken = "access";
    final String refreshToken = "refresh";
    final OAuth2AccessToken oauthAccessToken = mock(OAuth2AccessToken.class);
    final TimelineDataLoader loader = mock(TimelineDataLoader.class);
    final TwitterTimelineEventScheduler scheduler =
        spy(
            new TwitterTimelineEventScheduler(
                dateUtils, meterRegistry, loader, tweetFormatUtils, twitterCredentials));

    when(oauthAccessToken.getAccessToken()).thenReturn(accessToken);
    when(oauthAccessToken.getRefreshToken()).thenReturn(refreshToken);
    when(twitterApi.refreshToken()).thenThrow(new ApiException("test"));
    when(scheduler.getTwitterApi()).thenReturn(twitterApi);

    assertDoesNotThrow(() -> scheduler.refreshAccess());
    verify(twitterCredentials, times(0)).setTwitterOauth2AccessToken(accessToken);
    verify(twitterCredentials, times(0)).setTwitterOauth2RefreshToken(refreshToken);
    verify(meterRegistry, times(1))
        .counter(
            TwitterTimelineEventScheduler.TOKEN_REFRESH_COUNTER_NAME,
            Set.of(new ImmutableTag("result", "failure")));
  }
}
