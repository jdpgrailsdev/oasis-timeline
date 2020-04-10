package com.jdpgrailsdev.oasis.timeline.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtils {

    public String today() {
        final ZonedDateTime today = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        return String.format("%s %d", today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), today.getDayOfMonth());
    }
}
