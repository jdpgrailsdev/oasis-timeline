package com.jdpgrailsdev.oasis.timeline.util;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/** Helper class for interaction with the Twitter API and authentication. */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TwitterApiUtils {

  private static final Logger log = LoggerFactory.getLogger(TwitterApiUtils.class);

  private final Lock lock;
  private final TwitterCredentialsOAuth2 twitterCredentials;

  public TwitterApiUtils(final TwitterCredentialsOAuth2 twitterCredentials) {
    this.lock = new ReentrantLock();
    this.twitterCredentials = twitterCredentials;
  }

  /**
   * Creates and returns a new {@link TwitterApi} instance built using the most recent authorization
   * credentials.
   *
   * @return A new {@link TwitterApi} instance.
   */
  public TwitterApi getTwitterApi() {
    return new TwitterApi(getTwitterCredentials());
  }

  /**
   * Returns a copy of the {@link TwitterCredentialsOAuth2} authorization credentials held by this
   * utility.
   *
   * @return A copy of the {@link TwitterCredentialsOAuth2}.
   */
  public TwitterCredentialsOAuth2 getTwitterCredentials() {
    try {
      lock.lock();
      return new TwitterCredentialsOAuth2(
          twitterCredentials.getTwitterOauth2ClientId(),
          twitterCredentials.getTwitterOAuth2ClientSecret(),
          twitterCredentials.getTwitterOauth2AccessToken(),
          twitterCredentials.getTwitterOauth2RefreshToken());
    } finally {
      lock.unlock();
    }
  }

  /**
   * Updates the access token used by {@link TwitterApi} clients created by this helper.
   *
   * @param accessToken The updated access tokens.
   * @return {@code true} if the token update is successful, {@code false} otherwise.
   * @throws ApiException if unable to validate the updated tokens.
   */
  public boolean updateAccessTokens(final OAuth2AccessToken accessToken) throws ApiException {
    try {
      lock.lock();
      if (accessToken != null) {
        if (StringUtils.hasText(accessToken.getAccessToken())) {
          twitterCredentials.setTwitterOauth2AccessToken(accessToken.getAccessToken());
        }
        if (StringUtils.hasText(accessToken.getRefreshToken())) {
          twitterCredentials.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());
        }
        validateTokens();
        log.info("Access tokens updated.");
        return true;
      } else {
        log.error("No access tokens provided.  Nothing to update.");
        return false;
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * Validates the recently refreshed tokens by making an API call.
   *
   * @throws ApiException if unable to make the API call.
   */
  private void validateTokens() throws ApiException {
    log.info("Validating refreshed tokens...");
    getTwitterApi().users().findMyUser().execute();
    log.info("Refreshed token validation complete.");
  }
}
