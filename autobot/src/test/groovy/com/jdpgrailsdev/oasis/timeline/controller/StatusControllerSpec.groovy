package com.jdpgrailsdev.oasis.timeline.controller

import spock.lang.Specification

class StatusControllerSpec extends Specification {

    StatusController controller

    def setup() {
        controller = new StatusController()
    }

    def "test that when status check endpoint is called, a value of 'OK' is returned"() {
        expect:
            controller.statusCheck() == 'OK'
    }
}
