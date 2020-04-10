package com.jdpgrailsdev.oasis.timeline.config

import com.jdpgrailsdev.oasis.timeline.mocks.MockDateUtils
import com.jdpgrailsdev.oasis.timeline.mocks.MockTwitter
import com.jdpgrailsdev.oasis.timeline.util.DateUtils

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

import twitter4j.Twitter

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import(ApplicationConfiguration)
class IntegrationTestConfiguration {

    @Bean
    DateUtils dateUtils() { new MockDateUtils() }

    @Bean
    Twitter twitterApi() { new MockTwitter() }
}
