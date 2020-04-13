package com.jdpgrailsdev.oasis.timeline.controller

import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler

import spock.lang.Specification

class EventPublisherControllerSpec extends Specification {

    def "test that when the controller is invoked, the underlying scheduler is called"() {
        setup:
            def scheduler = Mock(TwitterTimelineEventScheduler)
            def controller = new EventPublisherController(scheduler)
        when:
            controller.publishEvents()
        then:
            1 * scheduler.publishTimelineTweet()
    }
}