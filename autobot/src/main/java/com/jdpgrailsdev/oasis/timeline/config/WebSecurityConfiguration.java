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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/** Spring configuration for web security beans. */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            (request) ->
                request
                    .requestMatchers("/status/check", "/css/*", "/js/*")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin((form) -> form.loginPage("/login").permitAll())
        .logout((logout) -> logout.permitAll());
    return http.build();
  }
}
