package com.jdpgrailsdev.oasis.timeline.schedule

import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.ApiException
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.ImmutableTag
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.scheduling.annotation.Scheduled

private val logger = KotlinLogging.logger {}

internal const val REFRESH_RESULT_TAG_NAME = "result"
internal const val TOKEN_REFRESH_COUNTER_NAME = "oauth2TokenRefresh"
internal const val SUCCESS_RESULT = "success"
internal const val FAILURE_RESULT = "failure"

/** Spring scheduler that refreshes Oauth2 tokens on a fixed schedule. */
@SuppressFBWarnings("EI_EXPOSE_REP2")
class Oauth2Scheduler(
  private val meterRegistry: MeterRegistry,
  private val twitterApiUtils: TwitterApiUtils,
) {
  @Scheduled(cron = "0 0 */1 * * *")
  fun refreshAccessTokens() {
    var result = SUCCESS_RESULT
    try {
      logger.info { "Attempting to refresh access tokens..." }
      val accessToken = twitterApiUtils.twitterApi.refreshToken()
      if (twitterApiUtils.updateAccessTokens(accessToken)) {
        logger.info { "Automatic access token refresh completed." }
      } else {
        result = FAILURE_RESULT
        logger.warn {
          "Automatic access token refresh complete, but no access token was retrieved."
        }
      }
    } catch (e: ApiException) {
      result = FAILURE_RESULT
      logger.error(e) { "Unable to refresh access token." }
    } finally {
      meterRegistry
        .counter(TOKEN_REFRESH_COUNTER_NAME, setOf(ImmutableTag(REFRESH_RESULT_TAG_NAME, result)))
        .increment()
    }
  }
}
