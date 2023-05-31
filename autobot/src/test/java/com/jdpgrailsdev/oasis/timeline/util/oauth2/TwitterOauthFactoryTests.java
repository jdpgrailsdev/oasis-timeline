package com.jdpgrailsdev.oasis.timeline.util.oauth2;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import org.junit.jupiter.api.Test;

class TwitterOauthFactoryTests {

  @Test
  void testCreateCallback() {
    final TwitterCredentialsOAuth2 credentials = mock(TwitterCredentialsOAuth2.class);
    final TwitterOauthFactory factory = new TwitterOauthFactory();

    final TwitterRefreshTokenCallback callback = factory.createCallback(credentials, 100L);
    assertNotNull(callback, "Callback should not be null");
  }

  @Test
  void testCreateRefresher() {
    final TwitterRefreshTokenCallback callback = mock(TwitterRefreshTokenCallback.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterOauthFactory factory = new TwitterOauthFactory();

    final TwitterTokenRefresher refresher = factory.createTokenReferesher(callback, twitterApi);
    assertNotNull(refresher, "Refresher should not be null");
  }
}
