package com.jdpgrailsdev.oasis.timeline

import com.jdpgrailsdev.oasis.timeline.config.IntegrationTestConfiguration
import com.jdpgrailsdev.oasis.timeline.mocks.MockDateUtils
import com.jdpgrailsdev.oasis.timeline.mocks.MockTwitter
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle

import spock.lang.Specification

@ActiveProfiles('test')
@SpringBootTest(classes=[IntegrationTestConfiguration])
class EndToEndIntegrationSpec extends Specification {

    @Autowired
    TwitterTimelineEventScheduler scheduler

    @Autowired
    MockTwitter twitter

    @Autowired
    MockDateUtils dateUtils

    def "test that when the scheduler is invoked for a date with timeline events, the events are published to Twitter"() {
        setup:
            dateUtils.setToday('October 2')
        when:
            scheduler.publishTimelineTweet()
        then:
            twitter.tweets.size() == 1
            twitter.tweets.first() == 'On this date in 1995, (What\'s the Story) Morning Glory?, Oasis\' second studio album, is released on Creation Records.  The album would propel the band to a worldwide fame, selling over 12 million copies around the world.'
        cleanup:
            dateUtils.reset()
            twitter.reset()
    }

    def "test that when the scheduled task is invoked for each date on the calendar, the task is executed"() {
        when:
            LocalDate end = LocalDate.of(2021, 1, 1)
            for(LocalDate date = LocalDate.of(2020, 1, 1); date.isBefore(end); date = date.plusDays(1)) {
                ZonedDateTime localDate = date.atStartOfDay(ZoneId.systemDefault())
                def today = "${localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${localDate.getDayOfMonth()}"
                dateUtils.setToday(today)
                scheduler.publishTimelineTweet()
            }
        then:
            twitter.tweets.size() > 0
        cleanup:
            dateUtils.reset()
            twitter.reset()
    }
}
