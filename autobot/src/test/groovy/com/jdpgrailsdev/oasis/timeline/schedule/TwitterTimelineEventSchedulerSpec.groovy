package com.jdpgrailsdev.oasis.timeline.schedule

import com.fasterxml.jackson.databind.ObjectMapper
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
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
import spock.lang.Unroll
import twitter4j.StatusUpdate
import twitter4j.Twitter
import twitter4j.TwitterException

class TwitterTimelineEventSchedulerSpec extends Specification {

    DateUtils dateUtils

    MeterRegistry meterRegistry

    ITemplateEngine templateEngine

    TimelineDataLoader timelineDataLoader

    TweetFormatUtils tweetFormatUtils

    Twitter twitterApi

    TwitterTimelineEventScheduler scheduler

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
        tweetFormatUtils = new TweetFormatUtils(templateEngine, [] as Set)
        twitterApi = Mock(Twitter)
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
            ObjectMapper mapper = new ObjectMapper()
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
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
            1 * meterRegistry.counter(TwitterTimelineEventScheduler.PUBLISH_EXECUTION_COUNTER_NAME) >> { Mock(Counter) }
            4 * meterRegistry.counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED_COUNTER_NAME) >> { Mock(Counter) }
            4 * twitterApi.updateStatus(_)
    }

    def "test that when the scheduled task runs for a date with no events, no tweets are published"() {
        setup:
            ObjectMapper mapper = new ObjectMapper()
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
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
            1 * meterRegistry.counter(TwitterTimelineEventScheduler.PUBLISH_EXECUTION_COUNTER_NAME) >> { Mock(Counter) }
            0 * meterRegistry.counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED_COUNTER_NAME) >> { Mock(Counter) }
            0 * twitterApi.updateStatus(_)
    }

    def "test that when the publishing of a status update to Twitter fails, the exception is handled"() {
        setup:
            StatusUpdate statusUpdate = new StatusUpdate('status update')
            twitterApi.updateStatus(_) >> { throw new TwitterException('test') }
        when:
            scheduler.publishStatusUpdate(statusUpdate)
        then:
            notThrown(TwitterException)
            1 * meterRegistry.counter(TwitterTimelineEventScheduler.TIMELINE_EVENTS_PUBLISHED_FAILURE_COUNTER_NAME) >> { Mock(Counter) }
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

    @Unroll
    def "test that when a event text '#text' is used to generate status updates, the expected number of status updates #expected is generated"() {
        when:
            def statusUpdates = scheduler.generateStatusUpdates(text)
        then:
            statusUpdates.size() == expected
            statusUpdates.each { statusUpdate ->
                statusUpdate.getStatus().length() <= TweetFormatUtils.TWEET_LIMIT
            }
        where:
            text                                                                || expected
            'A word'.multiply(200)                                              || 5
            'A word'                                                            || 1
    }

    def "test that an event that exceeds the limit of characters is appropriately broken up into individual parts"() {
        setup:
            def text = 'On this date in 1994, after back and forth with fans during a gig at Riverside in Newcastle, UK, a fight breaks out on stage resulting in Noel Gallager damaging a 1960\'s sunburst Gibson Les Paul guitar given to him by Johnny Marr of The Smiths.  The band refuse to continue the show after 5 songs, leading to fans surrounding the band\'s van.  Noel also would require stitches after the attack.  The setlist includes the following songs: Columbia, Shakermaker, Fade Away, Digsy\'s Dinner, Live Forever, Bring It On Down (Noel Gallagher attacked on stage during song).'
        when:
            def statusUpdates = scheduler.generateStatusUpdates(text)
        then:
            statusUpdates.size() == Math.ceil(text.length()/TweetFormatUtils.TWEET_LIMIT)
            statusUpdates[0].getStatus().length() <= TweetFormatUtils.TWEET_LIMIT
            statusUpdates[0].getStatus() == 'On this date in 1994, after back and forth with fans during a gig at Riverside in Newcastle, UK, a fight breaks out on stage resulting in Noel Gallager damaging a 1960\'s sunburst Gibson Les Paul guitar given to him by Johnny Marr of The Smiths.  The band refuse to continue the...'
            statusUpdates[1].getStatus().length() <= TweetFormatUtils.TWEET_LIMIT
            statusUpdates[1].getStatus() == '...show after 5 songs, leading to fans surrounding the band\'s van.  Noel also would require stitches after the attack.  The setlist includes the following songs: Columbia, Shakermaker, Fade Away, Digsy\'s Dinner, Live Forever, Bring It On Down (Noel Gallagher attacked on stage...'
            statusUpdates[2].getStatus().length() <= TweetFormatUtils.TWEET_LIMIT
            statusUpdates[2].getStatus() == '...during song).'
    }
}
