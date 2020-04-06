package com.jdpgrailsdev.oasis.timeline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import io.micrometer.NewRelicRegistryConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.micrometer.newrelic.NewRelicRegistry;

@Configuration
public class MicrometerConfiguration {

    @Bean
    public MeterRegistry meterRegistry(@Value("${INSIGHTS_API_KEY}") final String insightsApiKey,
            @Value("${INSIGHTS_API_URI}") final String insightsApiUri,
            @Value("${NEW_RELIC_APP_NAME}") final String serviceName) {
        final NewRelicRegistryConfig newRelicConfig = new NewRelicRegistryConfig() {
            @Override
            public String apiKey() {
                return insightsApiKey;
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
                return insightsApiUri;
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
