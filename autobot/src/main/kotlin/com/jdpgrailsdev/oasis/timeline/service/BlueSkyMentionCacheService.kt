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

package com.jdpgrailsdev.oasis.timeline.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.config.BlueSkyContext
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

/**
 * Service that is responsible for maintaining a cache of BlueSky handles to resolved profile DID
 * values.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
class BlueSkyMentionCacheService(
  private val blueSkyClient: BlueSkyClient,
  private val blueSkyContext: BlueSkyContext,
) : InitializingBean {
  private lateinit var cache: LoadingCache<String, String?>

  override fun afterPropertiesSet() {
    cache =
      Caffeine
        .newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .maximumSize(1000L)
        .build(this::resolveMention)
  }

  /**
   * Loads all mentions contained in the injected [BlueSkyContext] into the cache, resolving the
   * mentioned BlueSky handle to its profile's DID value via the BlueSky API.
   */
  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
  fun loadCache() {
    blueSkyContext.getMentions().forEach { mention ->
      try {
        cache.get(mention.value)
      } catch (e: Exception) {
        logger.warn(e) { "Failed to load cache entry for $mention." }
      }
    }
  }

  /**
   * Returns the DID value associated with the provided mention handle.
   *
   * @param mention A BlueSky mention handle.
   * @return The associated DID value or null if unable to resolve it.
   */
  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
  fun resolveDidForMention(mention: String) = cache.get(mention)

  private fun resolveMention(mention: String): String? =
    try {
      blueSkyClient.getProfile(handle = mention).did
    } catch (e: Exception) {
      logger.warn(e) { "Failed to resolve mention $mention." }
      null
    }
}
