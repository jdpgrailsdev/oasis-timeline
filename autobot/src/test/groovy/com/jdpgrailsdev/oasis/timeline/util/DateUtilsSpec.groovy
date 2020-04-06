package com.jdpgrailsdev.oasis.timeline.util

import java.time.LocalDate
import java.time.ZoneId

import spock.lang.Specification

class DateUtilsSpec extends Specification {

    def "test that when today\'s date is generated, the correct Instant is returned"() {
        setup:
            DateUtils dateUtils = new DateUtils()
        expect:
            dateUtils.today() == LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
    }
}
