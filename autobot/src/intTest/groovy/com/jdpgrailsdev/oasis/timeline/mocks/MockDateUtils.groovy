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
