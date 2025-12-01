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

package com.jdpgrailsdev.oasis.timeline.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyFacetType
import com.jdpgrailsdev.oasis.timeline.service.BlueSkyMentionCacheService
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Suppress("UNUSED")
@Configuration
class BlueSkyConfiguration {
  @Bean
  @Qualifier("blueSkyOkhttpClient")
  fun blueSkyOkhttpClient(): OkHttpClient = OkHttpClient()

  @Bean
  fun blueSkyClient(
    @Qualifier("blueSkyOkhttpClient") okHttpClient: OkHttpClient,
    objectMapper: ObjectMapper,
    @Value($$"${bluesky.url}") blueSkyUrl: String,
    @Value($$"${bluesky.public-url}") publicBlueSkyUrl: String,
    @Value($$"${bluesky.credentials.handle}") blueSkyHandle: String,
    @Value($$"${bluesky.credentials.password}") blueSkyPassword: String,
  ): BlueSkyClient =
    BlueSkyClient(
      blueSkyUrl = blueSkyUrl,
      blueSkyHandle = blueSkyHandle,
      blueSkyPassword = blueSkyPassword,
      client = okHttpClient,
      mapper = objectMapper,
      publicBlueSkyUrl = publicBlueSkyUrl,
    )

  @Bean
  fun blueSkyMentionCacheService(
    blueSkyClient: BlueSkyClient,
    blueSkyContext: BlueSkyContext,
  ): BlueSkyMentionCacheService = BlueSkyMentionCacheService(blueSkyClient = blueSkyClient, blueSkyContext = blueSkyContext)

  @Bean
  @Qualifier("blueSkyResolverMap")
  fun blueSkyResolverMap(blueSkyMentionCacheService: BlueSkyMentionCacheService): Map<BlueSkyFacetType, (mention: String) -> String?> =
    mapOf(BlueSkyFacetType.MENTION to blueSkyMentionCacheService::resolveDidForMention)
}
