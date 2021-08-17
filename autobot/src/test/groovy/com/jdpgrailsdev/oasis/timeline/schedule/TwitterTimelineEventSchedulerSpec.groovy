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
package com.jdpgrailsdev.oasis.timeline.schedule

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.jdpgrailsdev.oasis.timeline.config.TweetContext
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.data.Tweet
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.thymeleaf.ITemplateEngine

import java.time.Instant

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import spock.lang.Specification
import twitter4j.Status
import twitter4j.StatusUpdate
import twitter4j.Twitter
import twitter4j.TwitterException

class TwitterTimelineEventSchedulerSpec extends Specification {

    DateUtils dateUtils

    MeterRegistry meterRegistry

    ITemplateEngine templateEngine

    Status tweetStatus

    TimelineDataLoader timelineDataLoader

    TweetContext tweetContext

    TweetFormatUtils tweetFormatUtils

    Twitter twitterApi

    TwitterTimelineEventScheduler scheduler

    ObjectMapper objectMapper

    def setup() {
        dateUtils = Mock(DateUtils) {
            today() >> { 'January 1' }
        }
        meterRegistry = Mock(MeterRegistry) {
            counter(_) >> { Mock(Counter) }
            timer(_) >> { Mock(Timer) }
        }
        templateEngine = Mock(ITemplateEngine) {
            process(_,_) >> { 'This is a template string.' }
        }
        timelineDataLoader = Mock(TimelineDataLoader) {
            getHistory(_) >> { [] }
        }
        tweetContext = Mock(TweetContext) {
            getHashtags() >> { ['hashtag1', 'hashtag2'] as Set }
            getMentions() >> { [:] }
            getUncapitalizeExclusions() >> { ['Proper Noun'] as Set }
            getUncapitalizeExclusions() >> { [] as Set }
        }
        tweetFormatUtils = new TweetFormatUtils(templateEngine, tweetContext)
        tweetStatus = Mock(Status) {
            getId() >> { 12345l }
        }
        twitterApi = Mock(Twitter) {
            updateStatus(_) >> { tweetStatus }
        }

        objectMapper = new ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)

        scheduler = new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTimelineDataLoader(timelineDataLoader)
            .withTweetFormatUtils(tweetFormatUtils)
            .withTwitter(twitterApi)
            .build()
    }

    def "test that when the scheduled task runs, tweets are published for each timeline event"() {
        setup:
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
            loader.afterPropertiesSet()
            scheduler = new TwitterTimelineEventScheduler.Builder()
                .withDateUtils(dateUtils)
                .withMeterRegistry(meterRegistry)
                .withTimelineDataLoader(loader)
                .withTweetFormatUtils(tweetFormatUtils)
                .withTwitter(twitterApi)
                .build()
        when:
            scheduler.publishStatusUpdates()
        then:
            1 * meterRegistry.counter(TwitterTimelineEventScheduler.PUBLISH_EXECUTIONS) >> { Mock(Counter) }
            4 * meterRegistry.counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED) >> { Mock(Counter) }
            4 * twitterApi.updateStatus(_) >> { tweetStatus }
    }

    def "test that when the scheduled task runs for an event that produces a TwitterException, no tweets are published"() {
        setup:
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION) >> { [timelineDataResource] as Resource[] }
            }
            tweetFormatUtils = Mock(TweetFormatUtils) {
                generateTweet(_, _) >> { throw new TwitterException('test') }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
            loader.afterPropertiesSet()
            scheduler = new TwitterTimelineEventScheduler.Builder()
                .withDateUtils(dateUtils)
                .withMeterRegistry(meterRegistry)
                .withTimelineDataLoader(loader)
                .withTweetFormatUtils(tweetFormatUtils)
                .withTwitter(twitterApi)
                .build()
        when:
            scheduler.publishStatusUpdates()
        then:
            notThrown TwitterException
            1 * meterRegistry.counter(TwitterTimelineEventScheduler.PUBLISH_EXECUTIONS) >> { Mock(Counter) }
            0 * meterRegistry.counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED) >> { Mock(Counter) }
            0 * twitterApi.updateStatus(_) >> { tweetStatus }
    }

    def "test that when the scheduled task runs for a date with no events, no tweets are published"() {
        setup:
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
            loader.afterPropertiesSet()
            DateUtils dateUtils = Mock(DateUtils) {
                today() >> { Instant.now() }
            }
            scheduler = new TwitterTimelineEventScheduler.Builder()
                .withDateUtils(dateUtils)
                .withMeterRegistry(meterRegistry)
                .withTimelineDataLoader(loader)
                .withTweetFormatUtils(tweetFormatUtils)
                .withTwitter(twitterApi)
                .build()
        when:
            scheduler.publishStatusUpdates()
        then:
            1 * meterRegistry.counter(TwitterTimelineEventScheduler.PUBLISH_EXECUTIONS) >> { Mock(Counter) }
            0 * meterRegistry.counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED) >> { Mock(Counter) }
            0 * twitterApi.updateStatus(_)
    }

    def "test that when the publishing of a status update to Twitter fails, the exception is handled"() {
        setup:
            StatusUpdate statusUpdate = new StatusUpdate('status update')
            twitterApi = Mock(Twitter) {
                updateStatus(_) >> { throw new TwitterException('test') }
            }
            scheduler = new TwitterTimelineEventScheduler.Builder()
                .withDateUtils(dateUtils)
                .withMeterRegistry(meterRegistry)
                .withTimelineDataLoader(timelineDataLoader)
                .withTweetFormatUtils(tweetFormatUtils)
                .withTwitter(twitterApi)
                .build()
        when:
            def status = scheduler.publishStatusUpdate(statusUpdate)
        then:
            notThrown(TwitterException)
            status.isPresent() == false
            1 * meterRegistry.counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED_FAILURES) >> { Mock(Counter) }
    }

    def "test that when the scheduled task runs, the publish method is invoked"() {
        setup:
            Timer timer = Mock(Timer) {
                record(_) >> { it[0].run() }
            }
            meterRegistry.timer(_) >> { timer }
            scheduler = Spy(TwitterTimelineEventScheduler, constructorArgs:[dateUtils, meterRegistry, timelineDataLoader, tweetFormatUtils, twitterApi]) {
                publishStatusUpdates() >> { }
            }
        when:
            scheduler.publishTimelineTweet()
        then:
            1 * meterRegistry.timer(_) >> { timer }
            1 * scheduler.publishStatusUpdates()
    }

    def "test that when a tweet with additional replies is published, the expected number of status updates is created via the API"() {
        setup:
            def tweet = Mock(Tweet) {
                getMainTweet() >> { new StatusUpdate('main') }
                getReplies(_) >> { [new StatusUpdate('reply1'), new StatusUpdate('reply2')] }
            }
        when:
            def result = scheduler.publishTweet(tweet)
        then:
            result.isPresent() == true
            3 * scheduler.twitterApi.updateStatus(_) >> { tweetStatus }
    }

    def "test that when a tweet with additional replies is published but the main tweet fails to publish, the additional replies are skipped"() {
        setup:
            def tweet = Mock(Tweet) {
                getMainTweet() >> { new StatusUpdate('main') }
                getReplies(_) >> { [new StatusUpdate('reply1'), new StatusUpdate('reply2')] }
            }
            twitterApi = Mock(Twitter) {
                updateStatus(_) >> { throw new TwitterException('test') }
            }
            scheduler = new TwitterTimelineEventScheduler.Builder()
                .withDateUtils(dateUtils)
                .withMeterRegistry(meterRegistry)
                .withTimelineDataLoader(timelineDataLoader)
                .withTweetFormatUtils(tweetFormatUtils)
                .withTwitter(twitterApi)
                .build()
        when:
            def result = scheduler.publishTweet(tweet)
        then:
            result.isPresent() == false
    }
}
