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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.springframework.beans.factory.InitializingBean
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.util.Base64
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.SecretKeySpec

/** Collection of utility methods that support the encryption and decryption of string values. */
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
class EncryptionUtils(
  private val key: String,
  private val transformation: String,
) : InitializingBean {
  private lateinit var cipher: Cipher
  private lateinit var keySpec: SecretKeySpec

  @Throws(Exception::class)
  override fun afterPropertiesSet() {
    keySpec = SecretKeySpec(key.toByteArray(Charset.defaultCharset()), transformation)
    cipher = Cipher.getInstance(transformation)
  }

  /**
   * Encrypts the provided value.
   *
   * @param value The value to be encrypted.
   * @return The encrypted value.
   * @throws SecurityException if unable to perform the encryption.
   */
  @Throws(SecurityException::class)
  fun encrypt(value: String?): String {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, keySpec)
      return Base64
        .getEncoder()
        .encodeToString(cipher.doFinal(value?.toByteArray(Charset.defaultCharset())))
    } catch (e: IllegalBlockSizeException) {
      throw SecurityException("Unable to encrypt value", e)
    } catch (e: BadPaddingException) {
      throw SecurityException("Unable to encrypt value", e)
    } catch (e: InvalidKeyException) {
      throw SecurityException("Unable to encrypt value", e)
    }
  }

  /**
   * Decrypts the provided value.
   *
   * @param value The value to be decrypted.
   * @return The decrypted value.
   * @throws SecurityException if unable to perform the decryption.
   */
  @Throws(SecurityException::class)
  fun decrypt(value: String?): String {
    try {
      cipher.init(Cipher.DECRYPT_MODE, keySpec)
      return String(cipher.doFinal(Base64.getDecoder().decode(value)), Charset.defaultCharset())
    } catch (e: InvalidKeyException) {
      throw SecurityException("Unable to decrypt value", e)
    } catch (e: BadPaddingException) {
      throw SecurityException("Unable to decrypt value", e)
    } catch (e: IllegalBlockSizeException) {
      throw SecurityException("Unable to decrypt value", e)
    }
  }
}
