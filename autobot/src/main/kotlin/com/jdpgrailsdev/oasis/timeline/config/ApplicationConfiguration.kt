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

import com.github.javafaker.Faker
import com.jdpgrailsdev.oasis.timeline.context.StartupApplicationListener
import com.jdpgrailsdev.oasis.timeline.service.BlueSkyMentionCacheService
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService
import com.jdpgrailsdev.oasis.timeline.service.TextSearchService
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import org.springframework.boot.actuate.endpoint.Show
import org.springframework.boot.actuate.env.EnvironmentEndpoint
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.EnableScheduling

@Suppress("UNUSED")
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(
  value = [BlueSkyContext::class, PostContext::class, SanitizedKeys::class, TweetContext::class],
)
@EnableScheduling
@Import(
  ControllerConfiguration::class,
  BlueSkyConfiguration::class,
  JacksonConfiguration::class,
  MicrometerConfiguration::class,
  DataStoreConfiguration::class,
  SchedulerConfiguration::class,
  SearchConfiguration::class,
  ServiceConfiguration::class,
  ThymeleafConfiguration::class,
  PostConfiguration::class,
  TwitterConfiguration::class,
  WebMvcConfiguration::class,
  WebSecurityConfiguration::class,
)
class ApplicationConfiguration {
  /**
   * Custom [org.springframework.boot.actuate.endpoint.SanitizingFunction] implementation that
   * ensures secrets are not exposed via the [EnvironmentEndpoint].
   *
   * @param sanitizedKeys A set of keys whose associated values should be sanitized by the
   *   sanitizer.
   * @return the [EnvironmentSanitizingFunction] bean.
   */
  @Bean
  fun sanitizingFunction(sanitizedKeys: SanitizedKeys): EnvironmentSanitizingFunction = EnvironmentSanitizingFunction(sanitizedKeys)

  /**
   * Overrides the [EnvironmentEndpoint] bean to ensure that various configuration properties are
   * obfuscated.
   *
   * @param environment The runtime environment.
   * @return The [EnvironmentEndpoint] with sanitized properties.
   */
  @Bean
  fun environmentEndpoint(
    environment: Environment,
    sanitizingFunction: EnvironmentSanitizingFunction,
  ): EnvironmentEndpoint {
    /*
     * Custom override of the EnvironmentEndpoint Spring Boot actuator
     * to mask specific environment variables in addition to the normal set of masked keys.
     */
    return EnvironmentEndpoint(environment, setOf((sanitizingFunction)), Show.WHEN_AUTHORIZED)
  }

  /**
   * Custom [org.springframework.context.ApplicationListener] that performs operations on
   * application startup.
   *
   * @param dataStoreService The [DataStoreService] bean used to access the data store.
   * @param twitterApiUtils The [TwitterApiUtils] bean used to interact with the Twitter API.
   * @param blueSkyMentionCacheService The [BlueSkyMentionCacheService] bean used to build and
   *   maintain a cache of BlueSky handles to DID values.
   * @return The [StartupApplicationListener] bean.
   */
  @Bean
  fun startupApplicationListener(
    dataStoreService: DataStoreService,
    twitterApiUtils: TwitterApiUtils,
    blueSkyMentionCacheService: BlueSkyMentionCacheService,
    textSearchService: TextSearchService,
  ): StartupApplicationListener =
    StartupApplicationListener(
      blueSkyMentionCacheService = blueSkyMentionCacheService,
      dataStoreService = dataStoreService,
      textSearchService = textSearchService,
      twitterApiUtils = twitterApiUtils,
    )

  /**
   * Faker bean used to generate test events. This is utilized by the controller endpoint that
   * publishes test events to a social network in order to test formatting, replies, etc.
   *
   * @return A [Faker] instance bean.
   */
  @Bean fun faker(): Faker = Faker()
}
