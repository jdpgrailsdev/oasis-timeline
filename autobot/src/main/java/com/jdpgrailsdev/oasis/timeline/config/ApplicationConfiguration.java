package com.jdpgrailsdev.oasis.timeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdpgrailsdev.oasis.timeline.controller.StatusController;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.ITemplateEngine;

import io.micrometer.core.instrument.MeterRegistry;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import(value = {MicrometerConfiguration.class, ThymeleafConfiguration.class})
public class ApplicationConfiguration {

    @Bean
    public DateUtils dateUtils() {
        return new DateUtils();
    }

    @Bean
    public EnvironmentEndpoint environmentEndpoint(final Environment environment) {
        /*
         * Custom override of the EnvironmentEndpoint Spring Boot actuator
         * to mask the "ENCRYPTION_KEYS" environment variable in addition
         * to the normal set of masked keys.
         */
        final EnvironmentEndpoint endpoint = new EnvironmentEndpoint(environment);
        endpoint.setKeysToSanitize("INSIGHTS_API_KEY", "TWITTER_OAUTH_CONSUMER_KEY", "TWITTER_OAUTH_CONSUMER_SECRET", "TWITTER_OAUTH_ACCESS_TOKEN", "TWITTER_OAUTH_ACCESS_TOKEN_SECRET");
        return endpoint;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Twitter twitterApi(@Value("${TWITTER_OAUTH_CONSUMER_KEY}") final String oAuthConsumerKey,
            @Value("${TWITTER_OAUTH_CONSUMER_SECRET}") final String oAuthConsumerSecret,
            @Value("${TWITTER_OAUTH_ACCESS_TOKEN}") final String oAuthAccessToken,
            @Value("${TWITTER_OAUTH_ACCESS_TOKEN_SECRET}") final String oAuthAccessTokenSecret) {
        final twitter4j.conf.Configuration configuration = new ConfigurationBuilder()
            .setOAuthConsumerKey(oAuthConsumerKey)
            .setOAuthConsumerSecret(oAuthConsumerSecret)
            .setOAuthAccessToken(oAuthAccessTokenSecret)
            .setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
            .build();
        return new TwitterFactory(configuration).getInstance();
    }

    @Bean
    public TwitterTimelineEventScheduler twitterTimelineEventScheduler(final DateUtils dateUtils,
            final MeterRegistry meterRegistry,
            @Qualifier("textTemplateEngine") final ITemplateEngine templateEngine,
            final TimelineDataLoader timelineDataLoader,
            final Twitter twitterApi) {
        return new TwitterTimelineEventScheduler.Builder()
                .withDateUtils(dateUtils)
                .withMeterRegistry(meterRegistry)
                .withTemplateEngine(templateEngine)
                .withTimelineDataLoader(timelineDataLoader)
                .withTwitter(twitterApi)
                .build();
    }

    @Bean
    public ResourcePatternResolver timelineDataFileResourceResolver() {
        return new PathMatchingResourcePatternResolver(getClass().getClassLoader());
    }

    @Bean
    public TimelineDataLoader timelineDataLoader(final ObjectMapper objectMapper, final ResourcePatternResolver timelineDataFileResourceResolver) {
        return new TimelineDataLoader(objectMapper, timelineDataFileResourceResolver);
    }

    @Bean
    public StatusController statusController() {
        return new StatusController();
    }
}
