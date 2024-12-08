/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jdpgrailsdev.oasis.timeline.schedule;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
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

  private final MeterRegistry meterRegistry;
  private final TwitterApiUtils twitterApiUtils;

  public Oauth2Scheduler(final MeterRegistry meterRegistry, final TwitterApiUtils twitterApiUtils) {
    this.meterRegistry = meterRegistry;
    this.twitterApiUtils = twitterApiUtils;
  }

  @Scheduled(cron = "0 0 */1 * * *")
  public void refreshAccessTokens() {
    String result = "success";
    try {
      log.info("Attempting to refresh access tokens...");
      final OAuth2AccessToken accessToken = twitterApiUtils.getTwitterApi().refreshToken();
      if (twitterApiUtils.updateAccessTokens(accessToken)) {
        log.info("Automatic access token refresh completed.");
      } else {
        result = "failure";
        log.warn("Automatic access token refresh complete, but no access token was retrieved.");
      }
    } catch (final ApiException e) {
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
