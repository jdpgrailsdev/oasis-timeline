package com.jdpgrailsdev.oasis.timeline.util;

import com.google.common.annotations.VisibleForTesting;
import com.jdpgrailsdev.oasis.timeline.util.oauth2.TwitterOauthFactory;
import com.jdpgrailsdev.oasis.timeline.util.oauth2.TwitterRefreshTokenCallback;
import com.jdpgrailsdev.oasis.timeline.util.oauth2.TwitterTokenRefresher;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Factory used to create {@link TwitterApi} clients on demand. This is necessary because as of the
 * Twitter API 2.0, you must use OAuth2 to authenticate. That requires the refreshing of
 * access/refresh tokens periodically.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TwitterApiClientFactory {

  private final TwitterCredentialsOAuth2 twitterCredentials;
  private final TwitterOauthFactory twitterOauthFactory;
  private final Long refreshWaitTimeMs;

  public TwitterApiClientFactory(
      final TwitterCredentialsOAuth2 twitterCredentials,
      final TwitterOauthFactory twitterOauthFactory,
      final Long refreshWaitTimeMs) {
    this.twitterCredentials = twitterCredentials;
    this.twitterOauthFactory = twitterOauthFactory;
    this.refreshWaitTimeMs = refreshWaitTimeMs;
  }

  /**
   * Creates a new {@link TwitterApi} client with the access tokens refreshed.
   *
   * @return The {@link TwitterApi} client.
   * @throws ApiException if unable to create the {@link TwitterApi} client.
   */
  public TwitterApi createTwitterApi() throws ApiException {
    final TwitterRefreshTokenCallback callback =
        twitterOauthFactory.createCallback(twitterCredentials, refreshWaitTimeMs);
    final TwitterApi twitterApi = createTwitterApi(twitterCredentials);
    final TwitterTokenRefresher twitterTokenRefresher =
        twitterOauthFactory.createTokenReferesher(callback, twitterApi);
    twitterTokenRefresher.refreshToken();
    return twitterApi;
  }

  @VisibleForTesting
  TwitterApi createTwitterApi(final TwitterCredentialsOAuth2 twitterCredentials) {
    return new TwitterApi(twitterCredentials);
  }
}
