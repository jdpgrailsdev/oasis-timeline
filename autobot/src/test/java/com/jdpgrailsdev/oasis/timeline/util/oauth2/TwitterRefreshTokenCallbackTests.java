package com.jdpgrailsdev.oasis.timeline.util.oauth2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import org.junit.jupiter.api.Test;

class TwitterRefreshTokenCallbackTests {

  @Test
  void testCallback() {
    final String aToken = "1234456677";
    final String rToken = "ddfdsdfsdfss";
    final OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
    final TwitterCredentialsOAuth2 credentials =
        new TwitterCredentialsOAuth2("client id", "secret", null, null);
    final TwitterRefreshTokenCallback callback = new TwitterRefreshTokenCallback(credentials, 100L);

    when(accessToken.getAccessToken()).thenReturn(aToken);
    when(accessToken.getRefreshToken()).thenReturn(rToken);

    callback.onAfterRefreshToken(accessToken);
    callback.waitForRefresh();

    assertEquals(
        aToken,
        credentials.getTwitterOauth2AccessToken(),
        "Credentials should contain retrieved access token");
    assertEquals(
        rToken,
        credentials.getTwitterOauth2RefreshToken(),
        "Credentials should contain retrieved refresh token");
  }
}
