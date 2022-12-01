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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

/** Spring configuration for web security beans. */
@Configuration
public class WebSecurityConfiguration {

  private static final Logger log = LoggerFactory.getLogger(WebSecurityConfiguration.class);

  public WebSecurityCustomizer configure() {
    return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**");
  }

  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        (authz) -> {
          try {
            authz
                .requestMatchers("/status/check")
                .permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
          } catch (final Exception e) {
            log.error("Unable to configure authorization.", e);
          }
        });
    return http.build();
  }
}
