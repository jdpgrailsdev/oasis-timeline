package com.jdpgrailsdev.oasis.timeline.util;

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.InitializingBean;

/** Collection of utility methods that support the encryption and decryption of string values. */
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class EncryptionUtils implements InitializingBean {

  private Cipher cipher;
  private final String key;
  private SecretKeySpec keySpec;

  private final String transformation;

  public EncryptionUtils(final String key, final String transformation) {
    this.key = key;
    this.transformation = transformation;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    keySpec = new SecretKeySpec(key.getBytes(Charset.defaultCharset()), transformation);
    cipher = Cipher.getInstance(transformation);
  }

  /**
   * Encrypts the provided value.
   *
   * @param value The value to be encrypted.
   * @return The encrypted value.
   * @throws SecurityException if unable to perform the encryption.
   */
  public String encrypt(final String value) throws SecurityException {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);
      return Base64.getEncoder()
          .encodeToString(cipher.doFinal(value.getBytes(Charset.defaultCharset())));
    } catch (final IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
      throw new SecurityException("Unable to encrypt value", e);
    }
  }

  /**
   * Decrypts the provided value.
   *
   * @param value The value to be decrypted.
   * @return The decrypted value.
   * @throws SecurityException if unable to perform the decryption.
   */
  public String decrypt(final String value) throws SecurityException {
    try {
      cipher.init(Cipher.DECRYPT_MODE, keySpec);
      return new String(
          cipher.doFinal(Base64.getDecoder().decode(value)), Charset.defaultCharset());
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      throw new SecurityException("Unable to decrypt value", e);
    }
  }
}
