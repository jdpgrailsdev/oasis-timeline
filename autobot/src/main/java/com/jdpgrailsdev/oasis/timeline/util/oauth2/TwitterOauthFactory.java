package com.jdpgrailsdev.oasis.timeline.util.oauth2;

import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;

/** Factory class that aides in the creation of Twitter OAuth2 related helper classes. */
public class TwitterOauthFactory {

  public TwitterRefreshTokenCallback createCallback(
      final TwitterCredentialsOAuth2 twitterCredentials, final Long waitTimeMs) {
    return new TwitterRefreshTokenCallback(twitterCredentials, waitTimeMs);
  }

  public TwitterTokenRefresher createTokenReferesher(
      final TwitterRefreshTokenCallback callback, final TwitterApi twitterApi) {
    return new TwitterTokenRefresher(callback, twitterApi);
  }
}
