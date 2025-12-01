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

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.actuate.endpoint.SanitizableData
import org.springframework.core.env.PropertySource

internal class EnvironmentSanitizingFunctionTest {
  private lateinit var sanitizedKeys: SanitizedKeys
  private lateinit var sanitizingFunction: EnvironmentSanitizingFunction

  @BeforeEach
  fun setup() {
    sanitizedKeys = SanitizedKeys(setOf("INSERT_API_KEY"))
    sanitizingFunction = EnvironmentSanitizingFunction(sanitizedKeys)
  }

  @ParameterizedTest
  @CsvSource(
    "key,value",
    "INSERT_API_KEY,key-value",
    "INSERT_API_KEY,null",
    "INSERT_API_KEY,''",
    "key,null",
    "key,''",
  )
  fun testSanitization(
    key: String,
    value: String?,
  ) {
    val data = SanitizableData(mockk<PropertySource<*>>(), key, value)
    val expectedValue = if (sanitizedKeys.contains(key)) MASK else value
    val sanitizedData = sanitizingFunction.apply(data)
    assertEquals(key, sanitizedData.key)
    assertEquals(expectedValue, sanitizedData.value)
  }
}
