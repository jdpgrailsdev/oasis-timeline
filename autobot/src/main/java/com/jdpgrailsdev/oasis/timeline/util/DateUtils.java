package com.jdpgrailsdev.oasis.timeline.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateUtils {

    public Instant today() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
