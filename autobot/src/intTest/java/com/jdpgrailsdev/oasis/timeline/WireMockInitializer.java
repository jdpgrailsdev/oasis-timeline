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

package com.jdpgrailsdev.oasis.timeline;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Custom Spring {@link ApplicationContextInitializer} that create and stop a {@link
 * WireMockServer}s for integration test use.
 */
public class WireMockInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  public static Integer TEST_PORT = 9093;

  private static final Logger log = LoggerFactory.getLogger(WireMockInitializer.class);

  @Override
  public void initialize(final ConfigurableApplicationContext applicationContext) {
    // Create and start the WireMock server
    final WireMockServer wireMockServer = new WireMockServer(options().port(TEST_PORT));
    wireMockServer.addMockServiceRequestListener(this::logRequest);
    wireMockServer.start();

    // Register the WireMock server as a Spring bean
    applicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

    // Register a context listener to stop the WireMock server when the context is closed
    applicationContext.addApplicationListener(
        applicationEvent -> {
          if (applicationEvent instanceof ContextClosedEvent) {
            wireMockServer.stop();
          }
        });
  }

  private void logRequest(final Request request, final Response response) {
    log.info("Request URL: {}", request.getAbsoluteUrl());
    log.info("Request Method: {}", request.getMethod());
    log.info("Request Headers: \n{}", request.getHeaders());
    log.info("Request Body: \n{}", request.getBodyAsString());
    log.info("Response Status: {}", response.getStatus());
    log.info("Response Headers: \n{}", response.getHeaders());
    log.info("Response Body: \n{}", response.getBodyAsString());
  }
}
