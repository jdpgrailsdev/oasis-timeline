package com.jdpgrailsdev.oasis.timeline.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.api.UsersApi;
import com.twitter.clientlib.api.UsersApi.APIfindMyUserRequest;
import com.twitter.clientlib.model.Get2UsersMeResponse;
import org.junit.jupiter.api.Test;

/** Test suite for the {@link TwitterApiUtils} class. */
class TwitterApiUtilsTests {

  private static final String ACCESS_TOKEN = "access";
  private static final String REFRESH_TOKEN = "refresh";

  @Test
  void testGetTwitterApi() {
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);
    final TwitterApiUtils twitterApiUtils = new TwitterApiUtils(twitterCredentials);
    final TwitterApi twitterApi = twitterApiUtils.getTwitterApi();
    assertNotNull(twitterApi, "TwitterApi must not be null.");
  }

  @Test
  void testUpdateAccessTokens() throws ApiException {
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final Get2UsersMeResponse get2UsersMeResponse = mock(Get2UsersMeResponse.class);
    final APIfindMyUserRequest apiFindMyUserRequest = mock(APIfindMyUserRequest.class);
    final UsersApi usersApi = mock(UsersApi.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(ACCESS_TOKEN);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(REFRESH_TOKEN);
    when(apiFindMyUserRequest.execute()).thenReturn(get2UsersMeResponse);
    when(usersApi.findMyUser()).thenReturn(apiFindMyUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);

    final TwitterApiUtils twitterApiUtils = spy(new TwitterApiUtils(twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertTrue(
        twitterApiUtils.updateAccessTokens(oAuth2AccessToken),
        "Access tokens should update successfully");
    verify(twitterCredentials, times(1)).setTwitterOauth2AccessToken(ACCESS_TOKEN);
    verify(twitterCredentials, times(1)).setTwitterOauth2RefreshToken(REFRESH_TOKEN);
  }

  @Test
  void testUpdateAccessTokensBlankAccessToken() throws ApiException {
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final Get2UsersMeResponse get2UsersMeResponse = mock(Get2UsersMeResponse.class);
    final APIfindMyUserRequest apiFindMyUserRequest = mock(APIfindMyUserRequest.class);
    final UsersApi usersApi = mock(UsersApi.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(null);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(REFRESH_TOKEN);
    when(apiFindMyUserRequest.execute()).thenReturn(get2UsersMeResponse);
    when(usersApi.findMyUser()).thenReturn(apiFindMyUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);

    final TwitterApiUtils twitterApiUtils = spy(new TwitterApiUtils(twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertTrue(
        twitterApiUtils.updateAccessTokens(oAuth2AccessToken),
        "Access tokens should update successfully");
    verify(twitterCredentials, times(0)).setTwitterOauth2AccessToken(null);
    verify(twitterCredentials, times(1)).setTwitterOauth2RefreshToken(REFRESH_TOKEN);
  }

  @Test
  void testUpdateAccessTokensBlankRefreshToken() throws ApiException {
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final Get2UsersMeResponse get2UsersMeResponse = mock(Get2UsersMeResponse.class);
    final APIfindMyUserRequest apiFindMyUserRequest = mock(APIfindMyUserRequest.class);
    final UsersApi usersApi = mock(UsersApi.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(ACCESS_TOKEN);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(null);
    when(apiFindMyUserRequest.execute()).thenReturn(get2UsersMeResponse);
    when(usersApi.findMyUser()).thenReturn(apiFindMyUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);

    final TwitterApiUtils twitterApiUtils = spy(new TwitterApiUtils(twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertTrue(
        twitterApiUtils.updateAccessTokens(oAuth2AccessToken),
        "Access tokens should update successfully");
    verify(twitterCredentials, times(1)).setTwitterOauth2AccessToken(ACCESS_TOKEN);
    verify(twitterCredentials, times(0)).setTwitterOauth2RefreshToken(null);
  }

  @Test
  void testUpdateNullAccessTokens() throws ApiException {
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);
    final TwitterApiUtils twitterApiUtils = spy(new TwitterApiUtils(twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertFalse(
        twitterApiUtils.updateAccessTokens(null), "No update necessary when access token is null.");
    verify(twitterCredentials, times(0)).setTwitterOauth2AccessToken(ACCESS_TOKEN);
    verify(twitterCredentials, times(0)).setTwitterOauth2RefreshToken(REFRESH_TOKEN);
  }

  @Test
  void testUpdateAccessTokensFailedValidation() throws ApiException {
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    final APIfindMyUserRequest apiFindMyUserRequest = mock(APIfindMyUserRequest.class);
    final UsersApi usersApi = mock(UsersApi.class);
    final TwitterApi twitterApi = mock(TwitterApi.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(ACCESS_TOKEN);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(REFRESH_TOKEN);
    when(apiFindMyUserRequest.execute()).thenThrow(ApiException.class);
    when(usersApi.findMyUser()).thenReturn(apiFindMyUserRequest);
    when(twitterApi.users()).thenReturn(usersApi);

    final TwitterApiUtils twitterApiUtils = spy(new TwitterApiUtils(twitterCredentials));

    when(twitterApiUtils.getTwitterApi()).thenReturn(twitterApi);

    assertThrows(ApiException.class, () -> twitterApiUtils.updateAccessTokens(oAuth2AccessToken));
    verify(twitterCredentials, times(1)).setTwitterOauth2AccessToken(ACCESS_TOKEN);
    verify(twitterCredentials, times(1)).setTwitterOauth2RefreshToken(REFRESH_TOKEN);
  }
}
