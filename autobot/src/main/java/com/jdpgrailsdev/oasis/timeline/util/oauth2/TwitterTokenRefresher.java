package com.jdpgrailsdev.oasis.timeline.util.oauth2;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TwitterTokenRefresher {

  private static final Logger log = LoggerFactory.getLogger(TwitterTokenRefresher.class);

  private final TwitterRefreshTokenCallback callback;
  private final TwitterApi twitterApi;

  public TwitterTokenRefresher(
      final TwitterRefreshTokenCallback callback, final TwitterApi twitterApi) {
    this.callback = callback;
    this.twitterApi = twitterApi;
    this.twitterApi.addCallback(this.callback);
  }

  public void refreshToken() throws ApiException {
    log.info("Refreshing access tokens...");
    twitterApi.refreshToken();
    callback.waitForRefresh();
    log.info("Access token refresh complete.");
  }
}
