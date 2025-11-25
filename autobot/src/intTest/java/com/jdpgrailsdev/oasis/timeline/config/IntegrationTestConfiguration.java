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

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.github.fppt.jedismock.RedisServer;
import com.jdpgrailsdev.oasis.timeline.mocks.MockDateUtils;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Api;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import(ApplicationConfiguration.class)
public class IntegrationTestConfiguration {

  @SuppressWarnings("unused")
  @Bean
  public DateUtils dateUtils() {
    return new MockDateUtils();
  }

  @SuppressWarnings("unused")
  @Bean(destroyMethod = "stop")
  public RedisServer redisServer(@Value("${spring.data.redis.url}") final String url)
      throws IOException, URISyntaxException {
    final URI uri = new URI(url);
    return RedisServer.newRedisServer(uri.getPort(), InetAddress.getByName(uri.getHost())).start();
  }

  @Bean
  @Primary
  @SuppressWarnings({"AbbreviationAsWordInName", "PMD.AvoidAccessibilityAlteration", "unused"})
  public TwitterOAuth20Service twitterOAuth2Service(
      @Value("${TWITTER_API_BASE_PATH}") final String twitterBasePath,
      @Value("${server.base-url}") final String baseUrl,
      @Value("${oauth2.twitter.scopes}") final String scopes,
      final TwitterCredentialsOAuth2 twitterCredentials)
      throws InvocationTargetException, InstantiationException, IllegalAccessException {

    final Constructor<?> constructor = TwitterOAuth20Api.class.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    final TwitterOAuth20Api twitterOAuth20Api = spy((TwitterOAuth20Api) constructor.newInstance());
    when(twitterOAuth20Api.getAccessTokenEndpoint())
        .thenReturn(String.format("%s/2/oauth2/callback", twitterBasePath));

    try (MockedStatic<TwitterOAuth20Api> oauth2Api = mockStatic(TwitterOAuth20Api.class)) {
      oauth2Api.when(TwitterOAuth20Api::instance).thenReturn(twitterOAuth20Api);

      return new TwitterOAuth20Service(
          twitterCredentials.getTwitterOauth2ClientId(),
          twitterCredentials.getTwitterOAuth2ClientSecret(),
          String.format("%s/oauth2/callback", baseUrl),
          scopes);
    }
  }
}
