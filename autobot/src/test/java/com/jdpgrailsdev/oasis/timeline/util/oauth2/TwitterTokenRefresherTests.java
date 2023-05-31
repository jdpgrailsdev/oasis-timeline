package com.jdpgrailsdev.oasis.timeline.util.oauth2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import org.junit.jupiter.api.Test;

class TwitterTokenRefresherTests {

  @Test
  void testTokenRefresh() throws ApiException {
    final TwitterRefreshTokenCallback callback = mock(TwitterRefreshTokenCallback.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterTokenRefresher refresher = new TwitterTokenRefresher(callback, twitterApi);

    assertDoesNotThrow(() -> refresher.refreshToken());

    verify(twitterApi, times(1)).refreshToken();
    verify(callback, times(1)).waitForRefresh();
  }
}
