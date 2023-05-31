package com.jdpgrailsdev.oasis.timeline.util.oauth2;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.ApiClientCallback;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TwitterRefreshTokenCallback implements ApiClientCallback {

  private static final Logger log = LoggerFactory.getLogger(TwitterRefreshTokenCallback.class);

  private final TwitterCredentialsOAuth2 twitterCredentials;
  private final CountDownLatch latch;
  private final Long waitTimeMs;

  public TwitterRefreshTokenCallback(
      final TwitterCredentialsOAuth2 twitterCredentials, final Long waitTimeMs) {
    this.twitterCredentials = twitterCredentials;
    this.latch = new CountDownLatch(1);
    this.waitTimeMs = waitTimeMs;
  }

  @Override
  public void onAfterRefreshToken(final OAuth2AccessToken accessToken) {
    log.info(
        "Refreshed tokens (access = {}, refresh = {}).",
        accessToken.getAccessToken().replaceAll(".*", "*"),
        accessToken.getRefreshToken().replaceAll(".*", "*"));
    twitterCredentials.setTwitterOauth2AccessToken(accessToken.getAccessToken());
    twitterCredentials.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());
    latch.countDown();
  }

  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED")
  public void waitForRefresh() {
    try {
      latch.await(waitTimeMs, TimeUnit.MILLISECONDS);
    } catch (final InterruptedException e) {
      log.warn("Token refresh interrupted.", e);
    }
  }
}
