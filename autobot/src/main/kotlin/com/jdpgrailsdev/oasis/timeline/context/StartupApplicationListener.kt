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

package com.jdpgrailsdev.oasis.timeline.context

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException
import com.jdpgrailsdev.oasis.timeline.service.BlueSkyMentionCacheService
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService
import com.jdpgrailsdev.oasis.timeline.util.ACCESS_TOKEN_KEY
import com.jdpgrailsdev.oasis.timeline.util.REFRESH_TOKEN_KEY
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.util.StringUtils

private val logger = KotlinLogging.logger {}

/**
 * Custom Spring [ApplicationListener] that performs operations on application startup.
 *
 * <p>This listener retrieves any previously saved authentication credentials from the underlying
 * data store and transfers them to the in-memory credentials to ensure that re-authorization is not
 * required because of an application restart.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
class StartupApplicationListener(
  private val dataStoreService: DataStoreService,
  private val twitterApiUtils: TwitterApiUtils,
  private val blueSkyMentionCacheService: BlueSkyMentionCacheService,
) : ApplicationListener<ContextRefreshedEvent> {
  override fun onApplicationEvent(event: ContextRefreshedEvent) {
    refreshTwitterTokens()
    loadBlueSkyMentionsCache()
  }

  /** Refreshes the oAuth2 tokens used to interact with the Twitter API. */
  private fun refreshTwitterTokens() {
    val twitterCredentials = twitterApiUtils.getTwitterCredentials()
    if (!StringUtils.hasText(twitterCredentials.twitterOauth2AccessToken)) {
      try {
        val accessToken = dataStoreService.getValue(ACCESS_TOKEN_KEY)
        val refreshToken = dataStoreService.getValue(REFRESH_TOKEN_KEY)
        if (accessToken.isPresent && refreshToken.isPresent) {
          twitterApiUtils.updateInMemoryCredentials(accessToken.get(), refreshToken.get())
          logger.info { "In memory authentication tokens successfully updated from data store." }
        } else {
          logger.warn { "Authentication tokens not present in data store!" }
        }
      } catch (e: SecurityException) {
        logger.error(e) { "Unable to fetch authentication tokens from data store." }
      }
    } else {
      logger.info { "Authentication tokens already present in memory.  Nothing to do." }
    }
  }

  /** Loads the cache of BlueSky handles to DID values. */
  private fun loadBlueSkyMentionsCache() {
    blueSkyMentionCacheService.loadCache()
  }
}
