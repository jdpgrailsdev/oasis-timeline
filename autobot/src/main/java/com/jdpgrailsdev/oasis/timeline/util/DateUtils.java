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

package com.jdpgrailsdev.oasis.timeline.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/** Collection of date utility methods. */
public class DateUtils {

  /**
   * Returns today's date in the following format: July 1.
   *
   * @return Today's date.
   */
  public String today() {
    final ZonedDateTime today = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
    return formatDateTime(today);
  }

  /**
   * Formats the provided date time in the following format: July 1.
   *
   * @param dateTime A date time value as a {@link ZonedDateTime} instance.
   * @return The formatted date time value.
   */
  public String formatDateTime(final ZonedDateTime dateTime) {
    return String.format(
        "%s %d",
        dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
        dateTime.getDayOfMonth());
  }
}
