package com.jdpgrailsdev.oasis.timeline.schedule;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeException;
import dev.failsafe.RetryPolicy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/** Spring scheduler that refreshes Oauth2 tokens on a fixed schedule. */
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class Oauth2Scheduler {

  private static final Logger log = LoggerFactory.getLogger(Oauth2Scheduler.class);

  public static final String REFRESH_RESULT_TAG_NAME = "result";
  public static final String TOKEN_REFRESH_COUNTER_NAME = "oauth2TokenRefresh";

  private final RetryPolicy<Object> authRefreshRetryPolicy;
  private final MeterRegistry meterRegistry;
  private final TwitterApiUtils twitterApiUtils;

  public Oauth2Scheduler(
      final RetryPolicy<Object> authRefreshRetryPolicy,
      final MeterRegistry meterRegistry,
      final TwitterApiUtils twitterApiUtils) {
    this.authRefreshRetryPolicy = authRefreshRetryPolicy;
    this.meterRegistry = meterRegistry;
    this.twitterApiUtils = twitterApiUtils;
  }

  @Scheduled(cron = "0 0 */1 * * *")
  public void refreshAccessTokens() {
    String result = "success";
    try {
      log.info("Attempting to refresh access tokens...");
      final OAuth2AccessToken accessToken =
          Failsafe.with(authRefreshRetryPolicy)
              .onFailure(
                  e -> {
                    final Throwable exception = e.getException();
                    log.error(
                        "Attempt #{} failed to refresh access token.",
                        e.getAttemptCount(),
                        exception);
                  })
              .get(() -> twitterApiUtils.getTwitterApi().refreshToken());
      if (twitterApiUtils.updateAccessTokens(accessToken)) {
        log.info("Automatic access token refresh completed.");
      } else {
        result = "failure";
        log.warn("Automatic access token refresh complete, but no access token was retrieved.");
      }
    } catch (final ApiException | FailsafeException e) {
      result = "failure";
      log.error("Unable to refresh access token.", e);
    } finally {
      meterRegistry
          .counter(
              TOKEN_REFRESH_COUNTER_NAME, Set.of(new ImmutableTag(REFRESH_RESULT_TAG_NAME, result)))
          .increment();
    }
  }
}
