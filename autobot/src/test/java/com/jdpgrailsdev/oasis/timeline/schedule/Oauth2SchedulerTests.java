package com.jdpgrailsdev.oasis.timeline.schedule;

import static com.jdpgrailsdev.oasis.timeline.schedule.Oauth2Scheduler.REFRESH_RESULT_TAG_NAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.api.UsersApi;
import com.twitter.clientlib.model.Get2UsersMeResponse;
import com.twitter.clientlib.model.User;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test suite for the {@link Oauth2Scheduler} class. */
class Oauth2SchedulerTests {

  private MeterRegistry meterRegistry;

  @SuppressWarnings("PMD.SingularField")
  private TweetsApi tweetsApi;

  private TwitterApi twitterApi;
  private TwitterApiUtils twitterApiUtils;

  @BeforeEach
  void setup() {
    final Timer timer = mock(Timer.class);
    meterRegistry = mock(MeterRegistry.class);
    twitterApi = mock(TwitterApi.class);
    tweetsApi = mock(TweetsApi.class);
    twitterApiUtils = mock(TwitterApiUtils.class);

    when(meterRegistry.counter(anyString())).thenReturn(mock(Counter.class));
    when(meterRegistry.counter(anyString(), any(Iterable.class))).thenReturn(mock(Counter.class));
    when(meterRegistry.timer(anyString())).thenReturn(timer);
    when(timer.record(any(Supplier.class)))
        .thenAnswer(
            invocation -> {
              final Supplier supplier = invocation.getArgument(0);
              return supplier.get();
            });
    when(twitterApi.tweets()).thenReturn(tweetsApi);
    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);
  }

  @Test
  void testAutomaticAccessTokenRefresh() throws ApiException {
    final String accessToken = "access";
    final String refreshToken = "refresh";
    final OAuth2AccessToken oauthAccessToken = mock(OAuth2AccessToken.class);
    final UsersApi usersApi = mock(UsersApi.class);
    final UsersApi.APIfindMyUserRequest findUserRequest = mock(UsersApi.APIfindMyUserRequest.class);
    final Get2UsersMeResponse getUsersResponse = mock(Get2UsersMeResponse.class);
    final User user = mock(User.class);
    final String userId = "123456";
    final Oauth2Scheduler scheduler = new Oauth2Scheduler(meterRegistry, twitterApiUtils);

    when(oauthAccessToken.getAccessToken()).thenReturn(accessToken);
    when(oauthAccessToken.getRefreshToken()).thenReturn(refreshToken);
    when(user.getId()).thenReturn(userId);
    when(getUsersResponse.getData()).thenReturn(user);
    when(findUserRequest.execute()).thenReturn(getUsersResponse);
    when(usersApi.findMyUser()).thenReturn(findUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);
    when(twitterApi.refreshToken()).thenReturn(oauthAccessToken);
    when(twitterApiUtils.updateAccessTokens(oauthAccessToken)).thenReturn(true);

    assertDoesNotThrow(() -> scheduler.refreshAccessTokens());
    verify(twitterApiUtils, times(1)).updateAccessTokens(oauthAccessToken);
    verify(meterRegistry, times(1))
        .counter(
            TwitterTimelineEventScheduler.TOKEN_REFRESH_COUNTER_NAME,
            Set.of(new ImmutableTag(REFRESH_RESULT_TAG_NAME, "success")));
  }

  @Test
  void testAutomaticAccessTokenRefreshNull() throws ApiException {
    final UsersApi usersApi = mock(UsersApi.class);
    final UsersApi.APIfindMyUserRequest findUserRequest = mock(UsersApi.APIfindMyUserRequest.class);
    final Get2UsersMeResponse getUsersResponse = mock(Get2UsersMeResponse.class);
    final User user = mock(User.class);
    final String userId = "123456";
    final Oauth2Scheduler scheduler = new Oauth2Scheduler(meterRegistry, twitterApiUtils);

    when(user.getId()).thenReturn(userId);
    when(getUsersResponse.getData()).thenReturn(user);
    when(findUserRequest.execute()).thenReturn(getUsersResponse);
    when(usersApi.findMyUser()).thenReturn(findUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);
    when(twitterApi.refreshToken()).thenReturn(null);
    when(twitterApiUtils.updateAccessTokens(null)).thenReturn(false);

    assertDoesNotThrow(() -> scheduler.refreshAccessTokens());
    verify(twitterApiUtils, times(1)).updateAccessTokens(null);
    verify(meterRegistry, times(1))
        .counter(
            TwitterTimelineEventScheduler.TOKEN_REFRESH_COUNTER_NAME,
            Set.of(new ImmutableTag(REFRESH_RESULT_TAG_NAME, "failure")));
  }

  @Test
  void testAutomaticAccessTokenRefreshFailure() throws ApiException {
    final String accessToken = "access";
    final String refreshToken = "refresh";
    final OAuth2AccessToken oauthAccessToken = mock(OAuth2AccessToken.class);
    final UsersApi usersApi = mock(UsersApi.class);
    final UsersApi.APIfindMyUserRequest findUserRequest = mock(UsersApi.APIfindMyUserRequest.class);
    final Get2UsersMeResponse getUsersResponse = mock(Get2UsersMeResponse.class);
    final User user = mock(User.class);
    final String userId = "123456";
    final Oauth2Scheduler scheduler = new Oauth2Scheduler(meterRegistry, twitterApiUtils);

    when(oauthAccessToken.getAccessToken()).thenReturn(accessToken);
    when(oauthAccessToken.getRefreshToken()).thenReturn(refreshToken);
    when(user.getId()).thenReturn(userId);
    when(getUsersResponse.getData()).thenReturn(user);
    when(findUserRequest.execute()).thenReturn(getUsersResponse);
    when(usersApi.findMyUser()).thenReturn(findUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);
    when(twitterApi.refreshToken()).thenThrow(new ApiException("test"));

    assertDoesNotThrow(() -> scheduler.refreshAccessTokens());
    verify(twitterApiUtils, times(0)).updateAccessTokens(oauthAccessToken);
    verify(meterRegistry, times(1))
        .counter(
            TwitterTimelineEventScheduler.TOKEN_REFRESH_COUNTER_NAME,
            Set.of(new ImmutableTag(REFRESH_RESULT_TAG_NAME, "failure")));
  }

  @Test
  void testAutomaticAccessTokenRefreshValidationFailure() throws ApiException {
    final String accessToken = "access";
    final String refreshToken = "refresh";
    final OAuth2AccessToken oauthAccessToken = mock(OAuth2AccessToken.class);
    final UsersApi usersApi = mock(UsersApi.class);
    final UsersApi.APIfindMyUserRequest findUserRequest = mock(UsersApi.APIfindMyUserRequest.class);
    final Oauth2Scheduler scheduler = new Oauth2Scheduler(meterRegistry, twitterApiUtils);

    when(oauthAccessToken.getAccessToken()).thenReturn(accessToken);
    when(oauthAccessToken.getRefreshToken()).thenReturn(refreshToken);
    when(findUserRequest.execute()).thenThrow(ApiException.class);
    when(usersApi.findMyUser()).thenReturn(findUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);
    when(twitterApi.refreshToken()).thenReturn(oauthAccessToken);
    when(twitterApiUtils.updateAccessTokens(oauthAccessToken)).thenThrow(ApiException.class);

    assertDoesNotThrow(() -> scheduler.refreshAccessTokens());
    verify(twitterApiUtils, times(1)).updateAccessTokens(oauthAccessToken);
    verify(meterRegistry, times(1))
        .counter(
            TwitterTimelineEventScheduler.TOKEN_REFRESH_COUNTER_NAME,
            Set.of(new ImmutableTag(REFRESH_RESULT_TAG_NAME, "failure")));
  }
}
