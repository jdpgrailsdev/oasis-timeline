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

package com.jdpgrailsdev.oasis.timeline.service

import com.jdpgrailsdev.oasis.timeline.util.EncryptionUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

private const val KEY: String = "key"
private const val PREFIX: String = "redis:prefix:"
private const val VALUE: String = "a value"

/** Test suite for the [DataStoreService] class. */
internal class DataStoreServiceTest {
  @Test
  @Throws(SecurityException::class)
  fun testRetrieveValueFromDataStore() {
    val key = KEY
    val value = VALUE
    val encryptionUtils: EncryptionUtils = mockk { every { decrypt(value) } returns value }
    val valueOperations: ValueOperations<String, String> = mockk()
    // Done this way to avoid confusing Kotlin with the dynamic get() call
    every { valueOperations.get(any()) } returns value
    val redisTemplate: RedisTemplate<String, String> =
      mockk {
        every { opsForValue() } returns valueOperations
      }

    val service = DataStoreService(encryptionUtils, redisTemplate, PREFIX)

    val actualValue = service.getValue(key)
    Assertions.assertTrue(actualValue.isPresent)
    Assertions.assertEquals(value, actualValue.get())
    verify(exactly = 1) { valueOperations.get(generateKey(PREFIX, key)) }
  }

  @Test
  @Throws(SecurityException::class)
  fun testRetrieveValueFromDataStoreMissing() {
    val key = KEY
    val encryptionUtils: EncryptionUtils = mockk()
    val valueOperations: ValueOperations<String, String> = mockk()
    // Done this way to avoid confusing Kotlin with the dynamic get() call
    every { valueOperations.get(any()) } returns null
    val redisTemplate: RedisTemplate<String, String> =
      mockk {
        every { opsForValue() } returns valueOperations
      }

    val service = DataStoreService(encryptionUtils, redisTemplate, PREFIX)

    val actualValue = service.getValue(key)
    Assertions.assertTrue(actualValue.isEmpty)
    verify(exactly = 1) { valueOperations.get(generateKey(PREFIX, key)) }
  }

  @Test
  @Throws(SecurityException::class)
  fun testRetrieveValueFromDataStoreDecryptionFailure() {
    val key = KEY
    val value = VALUE
    val encryptionUtils: EncryptionUtils =
      mockk {
        every { decrypt(value) } throws SecurityException()
      }
    val valueOperations: ValueOperations<String, String> = mockk()
    // Done this way to avoid confusing Kotlin with the dynamic get() call
    every { valueOperations.get(any()) } returns value
    val redisTemplate: RedisTemplate<String, String> =
      mockk {
        every { opsForValue() } returns valueOperations
      }

    val service = DataStoreService(encryptionUtils, redisTemplate, PREFIX)

    Assertions.assertThrows(SecurityException::class.java) { service.getValue(key) }
    verify(exactly = 1) { valueOperations.get(generateKey(PREFIX, key)) }
  }

  @Test
  @Throws(SecurityException::class)
  fun testSaveValueToDataStore() {
    val key = KEY
    val value = VALUE
    val encryptionUtils: EncryptionUtils =
      mockk {
        every { decrypt(value) } returns value
        every { encrypt(value) } returns value
      }
    val valueOperations: ValueOperations<String, String> = mockk(relaxed = true)
    val redisTemplate: RedisTemplate<String, String> =
      mockk {
        every { opsForValue() } returns valueOperations
      }

    val service = DataStoreService(encryptionUtils, redisTemplate, PREFIX)

    Assertions.assertDoesNotThrow { service.setValue(key, value) }
    verify(exactly = 1) { valueOperations.set(generateKey(PREFIX, key), value) }
  }

  @Test
  @Throws(SecurityException::class)
  fun testSaveValueToDataStoreEncryptionFailure() {
    val key = KEY
    val value = VALUE
    val encryptionUtils: EncryptionUtils =
      mockk {
        every { encrypt(value) } throws SecurityException()
      }
    val valueOperations: ValueOperations<String, String> = mockk()
    // Done this way to avoid confusing Kotlin with the dynamic get() call
    every { valueOperations.get(any()) } returns value
    val redisTemplate: RedisTemplate<String, String> =
      mockk {
        every { opsForValue() } returns valueOperations
      }

    val service = DataStoreService(encryptionUtils, redisTemplate, PREFIX)

    Assertions.assertThrows(SecurityException::class.java) { service.setValue(key, value) }
    verify(exactly = 0) { valueOperations.set(generateKey(PREFIX, key), value) }
  }

  @Test
  fun testKeyGeneration() {
    val key = KEY
    Assertions.assertEquals("${PREFIX}key", generateKey(PREFIX, key))
    Assertions.assertEquals("${PREFIX}key", generateKey(PREFIX.dropLast(1), key))
  }
}
