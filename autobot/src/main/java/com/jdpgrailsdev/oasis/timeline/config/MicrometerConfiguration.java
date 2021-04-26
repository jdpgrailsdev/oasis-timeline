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


import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring configuration for Micrometer metrics. */
@Configuration
@AutoConfigureBefore({
    CompositeMeterRegistryAutoConfiguration.class,
    SimpleMetricsExportAutoConfiguration.class
})
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass(NewRelicRegistry.class)
public class MicrometerConfiguration {

    /**
     * Meter registry used to report metrics to New Relic.
     *
     * @param insertApiKey The New Relic insert API key value.
     * @param metricsApiUri The New Relic metrics API URL.
     * @param serviceName The application name.
     * @return The {@link MeterRegistry} bean.
     */
    @Bean
    public MeterRegistry meterRegistry(
            @Value("${INSERT_API_KEY}") final String insertApiKey,
            @Value("${METRICS_API_URI}") final String metricsApiUri,
            @Value("${NEW_RELIC_APP_NAME}") final String serviceName) {
        final NewRelicRegistryConfig newRelicConfig =
                new NewRelicRegistryConfig() {
                    @Override
                    public String apiKey() {
                        return insertApiKey;
                    }

                    @Override
                    public String serviceName() {
                        return serviceName;
                    }

                    @Override
                    public Duration step() {
                        return Duration.ofSeconds(1);
                    }

                    @Override
                    public String uri() {
                        return metricsApiUri;
                    }

                    @Override
                    public String get(final String k) {
                        return null; // accept the rest of the defaults
                    }
                };

        final NewRelicRegistry registry = NewRelicRegistry.builder(newRelicConfig).build();
        registry.start(new NamedThreadFactory("newrelic.micrometer.registry"));
        return registry;
    }
}
