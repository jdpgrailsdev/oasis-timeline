package com.jdpgrailsdev.oasis.timeline.util;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class for interaction with the Twitter API and authentication. */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record TwitterApiUtils(TwitterCredentialsOAuth2 twitterCredentials) {

  private static final Logger log = LoggerFactory.getLogger(TwitterApiUtils.class);

  public TwitterApi getTwitterApi() {
    return new TwitterApi(twitterCredentials);
  }

  /**
   * Updates the access token used by {@link TwitterApi} clients created by this helper.
   *
   * @param accessToken The updated access tokens.
   * @return {@code true} if the token update is successful, {@code false} otherwise.
   * @throws ApiException if unable to validate the updated tokens.
   */
  public boolean updateAccessTokens(final OAuth2AccessToken accessToken) throws ApiException {
    if (accessToken != null) {
      twitterCredentials.setTwitterOauth2AccessToken(accessToken.getAccessToken());
      twitterCredentials.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());
      validateTokens();
      log.info("Access tokens updated.");
      return true;
    } else {
      log.error("No access tokens provided.  Nothing to update.");
      return false;
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
