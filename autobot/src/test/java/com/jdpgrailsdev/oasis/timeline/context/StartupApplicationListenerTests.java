package com.jdpgrailsdev.oasis.timeline.context;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.context.event.ContextRefreshedEvent;

/** Test suite for the {@link StartupApplicationListener} class. */
class StartupApplicationListenerTests {

  private static final String ACCESS_TOKEN = "access";
  private static final String REFRESH_TOKEN = "refresh";

  @Test
  void testRetrieveAuthenticationCredentialsOnStartup() throws SecurityException {
    final String accessToken = ACCESS_TOKEN;
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(dataStoreService.getValue(TwitterApiUtils.ACCESS_TOKEN_KEY))
        .thenReturn(Optional.of(accessToken));
    when(dataStoreService.getValue(TwitterApiUtils.REFRESH_TOKEN_KEY))
        .thenReturn(Optional.of(refreshToken));
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(1)).updateInMemoryCredentials(accessToken, refreshToken);
  }

  @Test
  void testRetrieveAuthenticationCredentialsMissingAccessTokenOnStartup() throws SecurityException {
    final String accessToken = ACCESS_TOKEN;
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(dataStoreService.getValue(TwitterApiUtils.REFRESH_TOKEN_KEY))
        .thenReturn(Optional.of(refreshToken));
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(accessToken, refreshToken);
  }

  @Test
  void testRetrieveAuthenticationCredentialsMissingRefreshTokenOnStartup()
      throws SecurityException {
    final String accessToken = ACCESS_TOKEN;
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(dataStoreService.getValue(TwitterApiUtils.ACCESS_TOKEN_KEY))
        .thenReturn(Optional.of(accessToken));
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(accessToken, refreshToken);
  }

  @Test
  void testRetrieveAuthenticationCredentialsDecryptionFailureOnStartup() throws SecurityException {
    final String accessToken = ACCESS_TOKEN;
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(dataStoreService.getValue(TwitterApiUtils.ACCESS_TOKEN_KEY))
        .thenThrow(SecurityException.class);
    when(dataStoreService.getValue(TwitterApiUtils.REFRESH_TOKEN_KEY))
        .thenReturn(Optional.of(refreshToken));
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(accessToken, refreshToken);
  }

  @Test
  void testRetrieveAuthenticationCredentialsMissingOnStartup() {
    final String accessToken = ACCESS_TOKEN;
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(accessToken, refreshToken);
  }

  @Test
  void testRetrieveAuthenticationCredentialsAlreadyInMemoryOnStartup() throws SecurityException {
    final String accessToken = ACCESS_TOKEN;
    final String refreshToken = REFRESH_TOKEN;
    final ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
    final DataStoreService dataStoreService = mock(DataStoreService.class);
    final TwitterApiUtils twitterApiUtils = mock(TwitterApiUtils.class);
    final TwitterCredentialsOAuth2 twitterCredentials = mock(TwitterCredentialsOAuth2.class);

    when(twitterCredentials.getTwitterOauth2AccessToken()).thenReturn(ACCESS_TOKEN);
    when(twitterApiUtils.getTwitterCredentials()).thenReturn(twitterCredentials);

    final StartupApplicationListener listener =
        new StartupApplicationListener(dataStoreService, twitterApiUtils);

    assertDoesNotThrow(() -> listener.onApplicationEvent(event));
    verify(twitterApiUtils, times(0)).updateInMemoryCredentials(accessToken, refreshToken);
  }
}
