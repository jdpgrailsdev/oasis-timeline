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

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

private const val DEFAULT_TRANSFORM: String = "AES"

/** Test suite for the [EncryptionUtils] test. */
internal class EncryptionUtilsTest {
  @ParameterizedTest
  @ValueSource(
    strings = ["1234567890123456", "123456789012345678901234", "12345678901234567890123456789012"],
  )
  @Throws(Exception::class)
  fun testEncryptionDecryption(key: String?) {
    val unencrypted = "A quick brown fox jumps over the lazy dog"

    val encryptionUtils = EncryptionUtils(key, DEFAULT_TRANSFORM)
    encryptionUtils.afterPropertiesSet()

    val encrypted = encryptionUtils.encrypt(unencrypted)
    Assertions.assertNotEquals(unencrypted, encrypted)

    val unencrypted2 = encryptionUtils.decrypt(encrypted)
    Assertions.assertEquals(unencrypted, unencrypted2)
  }

  @Test
  @Throws(Exception::class)
  fun testEncryptionFailure() {
    val key = "invalidkey"
    val unencrypted = "A quick brown fox jumps over the lazy dog"

    val encryptionUtils = EncryptionUtils(key, DEFAULT_TRANSFORM)
    encryptionUtils.afterPropertiesSet()

    Assertions.assertThrows<SecurityException?>(SecurityException::class.java) {
      encryptionUtils.encrypt(unencrypted)
    }
  }

  @Test
  @Throws(Exception::class)
  fun testDecryptionFailure() {
    val key = "invalidkey"
    val encrypted = "ovRwynVL+VRzjS6rrFtiUsvRsNKAdTlU6eb4/t6tpPOSWGeULdG3nXEr7w7K4W23"

    val encryptionUtils = EncryptionUtils(key, DEFAULT_TRANSFORM)
    encryptionUtils.afterPropertiesSet()

    Assertions.assertThrows<SecurityException?>(SecurityException::class.java) {
      encryptionUtils.decrypt(encrypted)
    }
  }
}
