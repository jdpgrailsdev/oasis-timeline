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

import com.newrelic.telemetry.micrometer.NewRelicRegistry
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.util.NamedThreadFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.micrometer.metrics.autoconfigure.CompositeMeterRegistryAutoConfiguration
import org.springframework.boot.micrometer.metrics.autoconfigure.MetricsAutoConfiguration
import org.springframework.boot.micrometer.metrics.autoconfigure.export.simple.SimpleMetricsExportAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

/** Spring configuration for Micrometer metrics. */
@Suppress("UNUSED")
@Configuration
@AutoConfigureBefore(
  CompositeMeterRegistryAutoConfiguration::class,
  SimpleMetricsExportAutoConfiguration::class,
)
@AutoConfigureAfter(MetricsAutoConfiguration::class)
@ConditionalOnClass(NewRelicRegistry::class)
class MicrometerConfiguration {
  /**
   * Meter registry used to report metrics to New Relic.
   *
   * @param insertApiKey The New Relic insert API key value.
   * @param metricsApiUri The New Relic metrics API URL.
   * @param serviceName The application name.
   * @return The [MeterRegistry] bean.
   */
  @Bean
  fun meterRegistry(
    @Value($$"${INSERT_API_KEY}") insertApiKey: String,
    @Value($$"${METRICS_API_URI}") metricsApiUri: String,
    @Value($$"${NEW_RELIC_APP_NAME}") serviceName: String,
  ): MeterRegistry {
    val newRelicConfig: NewRelicRegistryConfig =
      NewRelicRegistryConfigImpl(insertApiKey, metricsApiUri, serviceName)
    val registry = NewRelicRegistry.builder(newRelicConfig).build()
    registry.start(NamedThreadFactory("newrelic.micrometer.registry"))
    return registry
  }

  private class NewRelicRegistryConfigImpl(
    val insertApiKey: String,
    val metricsApiUri: String,
    val serviceName: String,
  ) : NewRelicRegistryConfig {
    override fun apiKey(): String = insertApiKey

    override fun step(): Duration = Duration.ofMinutes(1)

    override fun uri(): String = metricsApiUri

    override fun get(key: String): String? {
      return null // accept the rest of the defaults
    }
  }
}
