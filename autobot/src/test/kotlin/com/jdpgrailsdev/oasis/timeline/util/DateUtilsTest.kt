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

package com.jdpgrailsdev.oasis.timeline.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

internal class DateUtilsTest {
  @Test
  @DisplayName("test that when today's date is generated, the correct Instant is returned")
  fun testGeneratingTodayDate() {
    val localDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
    val dateUtils = DateUtils()

    val expectedDate =
      "${localDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${localDate.dayOfMonth}"

    Assertions.assertEquals(expectedDate, dateUtils.today())
  }
}
