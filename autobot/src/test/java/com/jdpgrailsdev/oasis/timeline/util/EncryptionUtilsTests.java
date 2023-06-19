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
    final String transform = DEFAULT_TRANSFORM;
    final String unencrypted = "A quick brown fox jumps over the lazy dog";

    final EncryptionUtils encryptionUtils = new EncryptionUtils(key, transform);
    encryptionUtils.afterPropertiesSet();

    final String encrypted = encryptionUtils.encrypt(unencrypted);
    assertNotEquals(unencrypted, encrypted);

    final String unencrypted2 = encryptionUtils.decrypt(encrypted);
    assertEquals(unencrypted, unencrypted2);
  }

  @Test
  void testEncryptionFailure() throws Exception {
    final String key = "invalidkey";
    final String transform = DEFAULT_TRANSFORM;
    final String unencrypted = "A quick brown fox jumps over the lazy dog";

    final EncryptionUtils encryptionUtils = new EncryptionUtils(key, transform);
    encryptionUtils.afterPropertiesSet();

    assertThrows(SecurityException.class, () -> encryptionUtils.encrypt(unencrypted));
  }

  @Test
  void testDecryptionFailure() throws Exception {
    final String key = "invalidkey";
    final String transform = DEFAULT_TRANSFORM;
    final String encrypted = "ovRwynVL+VRzjS6rrFtiUsvRsNKAdTlU6eb4/t6tpPOSWGeULdG3nXEr7w7K4W23";

    final EncryptionUtils encryptionUtils = new EncryptionUtils(key, transform);
    encryptionUtils.afterPropertiesSet();

    assertThrows(SecurityException.class, () -> encryptionUtils.decrypt(encrypted));
  }
}
