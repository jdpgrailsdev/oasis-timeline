package com.jdpgrailsdev.oasis.timeline.util.oauth2;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TwitterTokenRefresher {

  private final TwitterRefreshTokenCallback callback;
  private final TwitterApi twitterApi;

  public TwitterTokenRefresher(
      final TwitterRefreshTokenCallback callback, final TwitterApi twitterApi) {
    this.callback = callback;
    this.twitterApi = twitterApi;
    this.twitterApi.addCallback(this.callback);
  }

  public void refreshToken() throws ApiException {
    twitterApi.refreshToken();
    callback.waitForRefresh();
  }
}
