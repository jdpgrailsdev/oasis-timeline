package com.jdpgrailsdev.oasis.timeline

import com.fasterxml.jackson.databind.ObjectMapper
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.util.DateUtils

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.thymeleaf.ITemplateEngine

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import spock.lang.Specification
import twitter4j.StatusUpdate
import twitter4j.Twitter
import twitter4j.TwitterException

class TwitterTimelineEventSchedulerSpec extends Specification {

    DateUtils dateUtils

    MeterRegistry meterRegistry

    ITemplateEngine templateEngine

    TimelineDataLoader timelineDataLoader

    Twitter twitterApi

    TwitterTimelineEventScheduler scheduler

    def setup() {
        dateUtils = Mock(DateUtils) {
            today() >> { LocalDate.parse('2020-01-01').atStartOfDay(ZoneId.systemDefault()).toInstant() }
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
        twitterApi = Mock(Twitter)
        scheduler = new TwitterTimelineEventScheduler.Builder()
            .withDateUtils(dateUtils)
            .withMeterRegistry(meterRegistry)
            .withTemplateEngine(templateEngine)
            .withTimelineDataLoader(timelineDataLoader)
            .withTwitter(twitterApi)
            .build()
    }

    def "test that when the scheduled task runs, tweets are published for each timeline event"() {
        setup:
            ObjectMapper mapper = new ObjectMapper()
            Resource resource = new ClassPathResource('/js/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(_) >> { [resource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
            loader.afterPropertiesSet()
            scheduler = new TwitterTimelineEventScheduler.Builder()
                .withDateUtils(dateUtils)
                .withMeterRegistry(meterRegistry)
                .withTemplateEngine(templateEngine)
                .withTimelineDataLoader(loader)
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
            Resource resource = new ClassPathResource('/js/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(_) >> { [resource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
            loader.afterPropertiesSet()
            DateUtils dateUtils = Mock(DateUtils) {
                today() >> { Instant.now() }
            }
            scheduler = new TwitterTimelineEventScheduler.Builder()
                .withDateUtils(dateUtils)
                .withMeterRegistry(meterRegistry)
                .withTemplateEngine(templateEngine)
                .withTimelineDataLoader(loader)
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
            scheduler = Spy(TwitterTimelineEventScheduler, constructorArgs:[dateUtils, meterRegistry, templateEngine, timelineDataLoader, twitterApi]) {
                publishStatusUpdates() >> { }
            }
        when:
            scheduler.publishTimelineTweet()
        then:
            1 * meterRegistry.timer(_) >> { timer }
            1 * scheduler.publishStatusUpdates()
    }
}
