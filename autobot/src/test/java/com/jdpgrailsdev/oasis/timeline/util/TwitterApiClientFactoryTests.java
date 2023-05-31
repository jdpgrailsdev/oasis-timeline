package com.jdpgrailsdev.oasis.timeline.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpgrailsdev.oasis.timeline.util.oauth2.TwitterOauthFactory;
import com.jdpgrailsdev.oasis.timeline.util.oauth2.TwitterRefreshTokenCallback;
import com.jdpgrailsdev.oasis.timeline.util.oauth2.TwitterTokenRefresher;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import org.junit.jupiter.api.Test;

class TwitterApiClientFactoryTests {

  private static final Long WAIT_TIME_MS = 100L;

  @Test
  void testTwitterApiClientCreation() throws ApiException {
    final TwitterOauthFactory twitterOauthFactory = mock(TwitterOauthFactory.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 credentials = mock(TwitterCredentialsOAuth2.class);
    final TwitterRefreshTokenCallback twitterRefreshTokenCallback =
        mock(TwitterRefreshTokenCallback.class);
    final TwitterTokenRefresher twitterTokenRefresher = mock(TwitterTokenRefresher.class);
    final TwitterApiClientFactory twitterApiClientFactory =
        spy(new TwitterApiClientFactory(credentials, twitterOauthFactory, WAIT_TIME_MS));

    when(twitterOauthFactory.createCallback(credentials, WAIT_TIME_MS))
        .thenReturn(twitterRefreshTokenCallback);
    when(twitterOauthFactory.createTokenReferesher(twitterRefreshTokenCallback, twitterApi))
        .thenReturn(twitterTokenRefresher);
    when(twitterApiClientFactory.createTwitterApi(credentials)).thenReturn(twitterApi);

    final TwitterApi created = twitterApiClientFactory.createTwitterApi();
    assertNotNull(created, "Created object should not be null");
    verify(twitterTokenRefresher, times(1)).refreshToken();
  }

  @Test
  void testTwitterApiClientCreationRefreshException() throws ApiException {
    final TwitterOauthFactory twitterOauthFactory = mock(TwitterOauthFactory.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 credentials = mock(TwitterCredentialsOAuth2.class);
    final TwitterRefreshTokenCallback twitterRefreshTokenCallback =
        mock(TwitterRefreshTokenCallback.class);
    final TwitterTokenRefresher twitterTokenRefresher = mock(TwitterTokenRefresher.class);
    final TwitterApiClientFactory twitterApiClientFactory =
        spy(new TwitterApiClientFactory(credentials, twitterOauthFactory, WAIT_TIME_MS));

    doThrow(new ApiException("test")).when(twitterTokenRefresher).refreshToken();
    when(twitterOauthFactory.createCallback(credentials, WAIT_TIME_MS))
        .thenReturn(twitterRefreshTokenCallback);
    when(twitterOauthFactory.createTokenReferesher(twitterRefreshTokenCallback, twitterApi))
        .thenReturn(twitterTokenRefresher);
    when(twitterApiClientFactory.createTwitterApi(credentials)).thenReturn(twitterApi);

    assertThrows(ApiException.class, () -> twitterApiClientFactory.createTwitterApi());
  }
}
