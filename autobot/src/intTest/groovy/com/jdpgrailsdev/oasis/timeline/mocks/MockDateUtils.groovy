package com.jdpgrailsdev.oasis.timeline.mocks

import com.jdpgrailsdev.oasis.timeline.util.DateUtils

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle

class MockDateUtils extends DateUtils {

    String today

    def reset() {
        ZonedDateTime localDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
        today = "${localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${localDate.getDayOfMonth()}"
    }

    def setToday(String today) {
        this.today = today
    }

    @Override
    String today() {
        if(!today) {
            reset()
        }
        today
    }
}
