package com.jdpgrailsdev.oasis.timeline.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import org.junit.jupiter.api.Test;

/** Test suite for the {@link TwitterApiUtils} class. */
class TwitterApiUtilsTests {

  private static final String ACCESS_TOKEN = "access";
  private static final String REFRESH_TOKEN = "refresh";

  @Test
  void testGetTwitterApi() {
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);
    final TwitterApiUtils twitterApiUtils =
        new TwitterApiUtils(dataStoreService, twitterCredentials);
    final TwitterApi twitterApi = twitterApiUtils.getTwitterApi();
    assertNotNull(twitterApi);
  }

  @Test
  void testUpdateAccessTokens() throws ApiException, SecurityException {
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(ACCESS_TOKEN);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(REFRESH_TOKEN);

    final TwitterApiUtils twitterApiUtils =
        spy(new TwitterApiUtils(dataStoreService, twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertTrue(twitterApiUtils.updateAccessTokens(oAuth2AccessToken));
    verify(twitterCredentials, times(1)).setTwitterOauth2AccessToken(ACCESS_TOKEN);
    verify(twitterCredentials, times(1)).setTwitterOauth2RefreshToken(REFRESH_TOKEN);
    verify(dataStoreService, times(1)).setValue(TwitterApiUtils.ACCESS_TOKEN_KEY, ACCESS_TOKEN);
    verify(dataStoreService, times(1)).setValue(TwitterApiUtils.REFRESH_TOKEN_KEY, REFRESH_TOKEN);
  }

  @Test
  void testUpdateAccessTokensBlankAccessToken() throws ApiException, SecurityException {
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(null);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(REFRESH_TOKEN);

    final TwitterApiUtils twitterApiUtils =
        spy(new TwitterApiUtils(dataStoreService, twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertFalse(twitterApiUtils.updateAccessTokens(oAuth2AccessToken));
    verify(twitterCredentials, times(0)).setTwitterOauth2AccessToken(null);
    verify(twitterCredentials, times(0)).setTwitterOauth2RefreshToken(REFRESH_TOKEN);
    verify(dataStoreService, times(0)).setValue(TwitterApiUtils.ACCESS_TOKEN_KEY, null);
    verify(dataStoreService, times(0)).setValue(TwitterApiUtils.REFRESH_TOKEN_KEY, REFRESH_TOKEN);
  }

  @Test
  void testUpdateAccessTokensBlankRefreshToken() throws ApiException, SecurityException {
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(ACCESS_TOKEN);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(null);

    final TwitterApiUtils twitterApiUtils =
        spy(new TwitterApiUtils(dataStoreService, twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertFalse(twitterApiUtils.updateAccessTokens(oAuth2AccessToken));
    verify(twitterCredentials, times(0)).setTwitterOauth2AccessToken(ACCESS_TOKEN);
    verify(twitterCredentials, times(0)).setTwitterOauth2RefreshToken(null);
    verify(dataStoreService, times(0)).setValue(TwitterApiUtils.ACCESS_TOKEN_KEY, ACCESS_TOKEN);
    verify(dataStoreService, times(0)).setValue(TwitterApiUtils.REFRESH_TOKEN_KEY, null);
  }

  @Test
  void testUpdateNullAccessTokens() throws ApiException, SecurityException {
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);
    final TwitterApiUtils twitterApiUtils =
        spy(new TwitterApiUtils(dataStoreService, twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertFalse(twitterApiUtils.updateAccessTokens(null));
    verify(twitterCredentials, times(0)).setTwitterOauth2AccessToken(ACCESS_TOKEN);
    verify(twitterCredentials, times(0)).setTwitterOauth2RefreshToken(REFRESH_TOKEN);
    verify(dataStoreService, times(0)).setValue(TwitterApiUtils.ACCESS_TOKEN_KEY, ACCESS_TOKEN);
    verify(dataStoreService, times(0)).setValue(TwitterApiUtils.REFRESH_TOKEN_KEY, REFRESH_TOKEN);
  }
}
