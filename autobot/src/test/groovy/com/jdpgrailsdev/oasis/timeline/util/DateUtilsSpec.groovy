package com.jdpgrailsdev.oasis.timeline.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle

import spock.lang.Specification

class DateUtilsSpec extends Specification {

    def "test that when today\'s date is generated, the correct Instant is returned"() {
        setup:
            ZonedDateTime localDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
            DateUtils dateUtils = new DateUtils()
        expect:
            dateUtils.today() == "${localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${localDate.getDayOfMonth()}"
    }
}
