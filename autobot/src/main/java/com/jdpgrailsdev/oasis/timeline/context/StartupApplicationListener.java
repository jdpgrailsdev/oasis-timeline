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

package com.jdpgrailsdev.oasis.timeline.context;

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

/**
 * Custom Spring {@link ApplicationListener} that performs operations on application startup.
 *
 * <p>This listener retrieves any previously saved authentication credentials from the underlying
 * data store and transfers them to the in-memory credentials to ensure that re-authorization is not
 * required because of an application restart.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger log = LoggerFactory.getLogger(StartupApplicationListener.class);

  private final DataStoreService dataStoreService;
  private final TwitterApiUtils twitterApiUtils;

  public StartupApplicationListener(
      final DataStoreService dataStoreService, final TwitterApiUtils twitterApiUtils) {
    this.dataStoreService = dataStoreService;
    this.twitterApiUtils = twitterApiUtils;
  }

  @Override
  public void onApplicationEvent(@Nonnull final ContextRefreshedEvent event) {
    final TwitterCredentialsOAuth2 twitterCredentials = twitterApiUtils.getTwitterCredentials();
    if (!StringUtils.hasText(twitterCredentials.getTwitterOauth2AccessToken())) {
      try {
        final Optional<String> accessToken =
            dataStoreService.getValue(TwitterApiUtils.ACCESS_TOKEN_KEY);
        final Optional<String> refreshToken =
            dataStoreService.getValue(TwitterApiUtils.REFRESH_TOKEN_KEY);
        if (accessToken.isPresent() && refreshToken.isPresent()) {
          twitterApiUtils.updateInMemoryCredentials(accessToken.get(), refreshToken.get());
          log.info("In memory authentication tokens successfully updated from data store.");
        } else {
          log.warn("Authentication tokens not present in data store!");
        }
      } catch (final SecurityException e) {
        log.error("Unable to fetch authentication tokens from data store.", e);
      }
    } else {
      log.info("Authentication tokens already present in memory.  Nothing to do.");
    }
  }
}
