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

  @Override
  public void initialize(final ConfigurableApplicationContext applicationContext) {
    // Create and start the WireMock server
    final WireMockServer wireMockServer = new WireMockServer(options().port(TEST_PORT));
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
}
