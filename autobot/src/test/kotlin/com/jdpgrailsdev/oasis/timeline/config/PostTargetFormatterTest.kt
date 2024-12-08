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

package com.jdpgrailsdev.oasis.timeline.config

import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class PostTargetFormatterTest {
  private lateinit var formatter: PostTargetFormatter

  @BeforeEach
  fun setUp() {
    formatter = PostTargetFormatter()
  }

  @ParameterizedTest
  @CsvSource(
    value =
      [
        "bluesky,BLUESKY",
        "blueSky,BLUESKY",
        "BLUESKY,BLUESKY",
        "bLuEsKy,BLUESKY",
        "twitter,TWITTER",
        "TWITTER,TWITTER",
        "tWiTtEr,TWITTER",
      ],
  )
  fun testConvertingStringToPostTarget(
    param: String,
    expected: PostTarget,
  ) {
    assertEquals(expected, formatter.convert(param))
  }
}
