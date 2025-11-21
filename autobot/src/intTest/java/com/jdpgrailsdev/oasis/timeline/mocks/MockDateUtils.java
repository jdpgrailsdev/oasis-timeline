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

package com.jdpgrailsdev.oasis.timeline.mocks;

import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.junit.platform.commons.util.StringUtils;

public class MockDateUtils extends DateUtils {

  private String today;

  public void reset() {
    final ZonedDateTime localDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
    today =
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            + " "
            + localDate.getDayOfMonth();
  }

  public void setToday(final String today) {
    this.today = today;
  }

  @NotNull
  @Override
  public String today() {
    if (StringUtils.isBlank(today)) {
      reset();
    }
    return today;
  }
}
