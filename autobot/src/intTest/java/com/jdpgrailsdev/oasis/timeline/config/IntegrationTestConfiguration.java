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

import com.jdpgrailsdev.oasis.timeline.mocks.MockDateUtils;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import(ApplicationConfiguration.class)
public class IntegrationTestConfiguration {

  @Bean
  public DateUtils dateUtils() {
    return new MockDateUtils();
  }

  @Bean
  public OkHttpClient.Builder mastodonClientBuilder()
      throws NoSuchAlgorithmException, KeyManagementException {
    final TrustManager[] trustAllCerts = {new AllTrustManager()};
    final SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
    builder.hostnameVerifier((hostname, session) -> true);
    return builder;
  }

  private static class AllTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(
        final java.security.cert.X509Certificate[] chain, final String authType) {
      // Intentionally unimplemented
    }

    @Override
    public void checkServerTrusted(
        final java.security.cert.X509Certificate[] chain, final String authType) {
      // Intentionally unimplemented
    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
      return new java.security.cert.X509Certificate[] {};
    }
  }
}
