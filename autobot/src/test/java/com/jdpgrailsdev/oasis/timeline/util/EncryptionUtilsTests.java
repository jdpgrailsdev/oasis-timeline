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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Test suite for the {@link EncryptionUtils} test. */
class EncryptionUtilsTests {

  private static final String DEFAULT_TRANSFORM = "AES";

  @ParameterizedTest
  @ValueSource(
      strings = {
        "1234567890123456",
        "123456789012345678901234",
        "12345678901234567890123456789012"
      })
  void testEncryptionDecryption(final String key) throws Exception {
    final String unencrypted = "A quick brown fox jumps over the lazy dog";

    final EncryptionUtils encryptionUtils = new EncryptionUtils(key, DEFAULT_TRANSFORM);
    encryptionUtils.afterPropertiesSet();

    final String encrypted = encryptionUtils.encrypt(unencrypted);
    assertNotEquals(unencrypted, encrypted);

    final String unencrypted2 = encryptionUtils.decrypt(encrypted);
    assertEquals(unencrypted, unencrypted2);
  }

  @Test
  void testEncryptionFailure() throws Exception {
    final String key = "invalidkey";
    final String unencrypted = "A quick brown fox jumps over the lazy dog";

    final EncryptionUtils encryptionUtils = new EncryptionUtils(key, DEFAULT_TRANSFORM);
    encryptionUtils.afterPropertiesSet();

    assertThrows(SecurityException.class, () -> encryptionUtils.encrypt(unencrypted));
  }

  @Test
  void testDecryptionFailure() throws Exception {
    final String key = "invalidkey";
    final String encrypted = "ovRwynVL+VRzjS6rrFtiUsvRsNKAdTlU6eb4/t6tpPOSWGeULdG3nXEr7w7K4W23";

    final EncryptionUtils encryptionUtils = new EncryptionUtils(key, DEFAULT_TRANSFORM);
    encryptionUtils.afterPropertiesSet();

    assertThrows(SecurityException.class, () -> encryptionUtils.decrypt(encrypted));
  }
}
