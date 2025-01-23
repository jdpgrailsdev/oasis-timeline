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

package com.jdpgrailsdev.oasis.timeline.config;

import com.github.javafaker.Faker;
import com.jdpgrailsdev.oasis.timeline.context.StartupApplicationListener;
import com.jdpgrailsdev.oasis.timeline.service.BlueSkyMentionCacheService;
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import java.util.Set;
import org.springframework.boot.actuate.endpoint.SanitizableData;
import org.springframework.boot.actuate.endpoint.SanitizingFunction;
import org.springframework.boot.actuate.endpoint.Show;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Main Spring application configuration. */
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(
    value = {BlueSkyContext.class, MastodonContext.class, PostContext.class, TweetContext.class})
@EnableScheduling
@Import({
  ControllerConfiguration.class,
  DataStoreConfiguration.class,
  BlueSkyConfiguration.class,
  JacksonConfiguration.class,
  MastodonConfiguration.class,
  MicrometerConfiguration.class,
  PostConfiguration.class,
  SchedulerConfiguration.class,
  ThymeleafConfiguration.class,
  TwitterConfiguration.class,
  WebMvcConfiguration.class,
  WebSecurityConfiguration.class
})
public class ApplicationConfiguration {

  private static final Set<String> SANITIZED_KEYS =
      Set.of(
          "INSERT_API_KEY",
          "NEW_RELIC_LICENSE_KEY",
          "SPRING_ACTUATOR_USERNAME",
          "SPRING_ACTUATOR_PASSWORD",
          "SPRING_DATA_REDIS_URL",
          "spring.data.redis.url",
          "SPRING_DATA_REDIS_SECURITY_KEY",
          "spring.data.redis.security.key",
          "SPRING_DATA_REDIS_SECURITY_TRANSFORMATION",
          "spring.data.redis.security.transformation",
          "spring.security.user.name",
          "spring.security.user.password",
          "TWITTER_OAUTH_CONSUMER_KEY",
          "TWITTER_OAUTH_CONSUMER_SECRET",
          "TWITTER_OAUTH_ACCESS_TOKEN",
          "TWITTER_OAUTH_ACCESS_TOKEN_SECRET",
          "TWITTER_OAUTH2_CLIENT_ID",
          "TWITTER_OAUTH2_CLIENT_SECRET",
          "TWITTER_OAUTH2_ACCESS_TOKEN",
          "TWITTER_OAUTH2_REFRESH_TOKEN",
          "BLUESKY_HANDLE",
          "BLUESKY_PASSWORD",
          "bluesky.credentials.handle",
          "bluesky.credentials.password",
          "oauth2.pkce.challenge");

  /**
   * Overrides the {@link EnvironmentEndpoint} bean to ensure that various configuration properties
   * are obfuscated.
   *
   * @param environment The runtime environment.
   * @return The {@link EnvironmentEndpoint} with sanitized properties.
   */
  @Bean
  public EnvironmentEndpoint environmentEndpoint(final Environment environment) {
    /*
     * Custom override of the EnvironmentEndpoint Spring Boot actuator
     * to mask specific environment variables in addition to the normal set of masked keys.
     */
    return new EnvironmentEndpoint(
        environment, Set.of(new EnvironmentSanitizingFunction()), Show.WHEN_AUTHORIZED);
  }

  /**
   * Custom {@link org.springframework.context.ApplicationListener} that performs operations on
   * application startup.
   *
   * @param dataStoreService The {@link DataStoreService} bean used to access the data store.
   * @param twitterApiUtils The {@link TwitterApiUtils} bean used to interact with the Twitter API.
   * @param blueSkyMentionCacheService The {@link BlueSkyMentionCacheService} bean used to build and
   *     maintain a cache of BlueSky handles to DID values.
   * @return The {@link StartupApplicationListener} bean.
   */
  @Bean
  public StartupApplicationListener startupApplicationListener(
      final DataStoreService dataStoreService,
      final TwitterApiUtils twitterApiUtils,
      final BlueSkyMentionCacheService blueSkyMentionCacheService) {
    return new StartupApplicationListener(
        dataStoreService, twitterApiUtils, blueSkyMentionCacheService);
  }

  /**
   * Faker bean used to generate test events. This is utilized by the controller endpoint that
   * publishes test events to a social network in order to test formatting, replies, etc.
   *
   * @return A {@link Faker} instance bean.
   */
  @Bean
  public Faker faker() {
    return new Faker();
  }

  private static final class EnvironmentSanitizingFunction implements SanitizingFunction {
    @Override
    public SanitizableData apply(final SanitizableData data) {
      if (SANITIZED_KEYS.contains(data.getKey())) {
        return new SanitizableData(data.getPropertySource(), data.getKey(), "********");
      } else {
        return data;
      }
    }
  }
}
