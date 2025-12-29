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

import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.config.MeterFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

/** Spring configuration for Micrometer metrics. */
@Suppress("UNUSED")
@Configuration
class MicrometerConfiguration {
  @Bean
  fun commonTagsMeterFilter(
    environment: Environment,
    @Value($$"${NEW_RELIC_APP_NAME}") serviceName: String,
  ) = MeterFilter.commonTags(
    mutableListOf(
      Tag.of("instrumentation.provider", "micrometer"),
      Tag.of("service.name", serviceName),
      Tag.of("environment", environment.activeProfiles.joinToString(",")),
    ),
  )
}
