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
package com.jdpgrailsdev.oasis.timeline

import com.jdpgrailsdev.oasis.timeline.config.IntegrationTestConfiguration
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import com.jdpgrailsdev.oasis.timeline.data.Tweet
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
            twitter.tweets.first() == "${TimelineDataType.releases.getEmoji()} #OnThisDay in 1995, '(What's the Story) Morning Glory?', @Oasis' second studio album, is released on Creation Records.  The album would propel the band to a worldwide fame, selling over 12 million copies around the world.\n\n#Oasis #TodayInMusic #britpop"
        cleanup:
            dateUtils.reset()
            twitter.reset()
    }

    def "test that when the scheduler is invoked for a date with timeline events and the generated tweet exceeds the size limit, the event is split and published to Twitter"() {
        setup:
            dateUtils.setToday('April 24')
        when:
            scheduler.publishTimelineTweet()
        then:
            twitter.tweets.size() == 3
            twitter.tweets[0] == "${TimelineDataType.releases.getEmoji()} #OnThisDay in 1995, 'Some Might Say', the first single from the forthcoming album '(What's The Story) Morning Glory?', is released on Creation Records.  It would go on to become @Oasis' first number one single and is the last recording to feature the original lineup.  The..."
            twitter.tweets[1] == '... single includes the b-sides \'Talk Tonight\', \'Acquiesce\' and \'Headshrinker\'.\n\n#Oasis #TodayInMusic #britpop'
        cleanup:
            dateUtils.reset()
            twitter.reset()
    }

    def "test that when the scheduler is invoked for a date with timeline events that should include mentions, the events are published to Twitter with the mentions included"() {
        setup:
            dateUtils.setToday('August 18')
        when:
            scheduler.publishTimelineTweet()
        then:
            twitter.tweets.size() == 5
            twitter.tweets[0] == "${TimelineDataType.gigs.getEmoji()} #OnThisDay in 1991, @Oasis perform their first gig under the name \"@Oasis\" at The Boardwalk in Manchester, UK.  At this point, the band is a 4-piece made up of Liam Gallagher, Paul \"Bonehead\" Arthurs, Paul \"Guigsy\" McGuigan and Tony McCarroll."
            twitter.tweets[1] == 'The Inspiral Carpets are in attendance, accompanied by roadie Noel Gallagher, who sees his brother\'s band perform live for the first time.\n\n@boneheadspage @liamGallagher @noelgallagher @TonyMcCarrolls #Oasis #TodayInMusic #britpop'
            twitter.tweets[2] == "${TimelineDataType.noteworthy.getEmoji()} #OnThisDay in 1996, Noel Gallagher joins Paul Weller and Jools Holland on stage at the first V Festival in Hylands Park, Chelmsford, UK to play 'Eye of the Storm'.  During the song, Noel asks the crowd to \"show your appreciation\" and informs..."
            twitter.tweets[3] == '... the crowd that "Alan White\'s brother" (Steve White) is on drums.  Paul Weller returns the favor by thanking "Mr. Liam Gallagher" after the jam.  It would be another nine years before @Oasis would finally appear at the festival in...'
            twitter.tweets[4] == '... 2005.\n\n@drummerwhitey @liamGallagher @noelgallagher #Oasis #TodayInMusic #britpop'
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
            twitter.tweets.each { tweet ->
                tweet.length() <= Tweet.TWEET_LIMIT
            }
        cleanup:
            dateUtils.reset()
            twitter.reset()
    }
}
