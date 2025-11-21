package com.jdpgrailsdev.oasis.timeline.service

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException
import com.jdpgrailsdev.oasis.timeline.util.EncryptionUtils
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import java.util.Optional

private val logger = KotlinLogging.logger {}

/**
 * Generates the data store key based on the provided prefix and user key.
 *
 * @param prefix The configured prefix for all entries in the data store.
 * @param userKey The user provided key value.
 * @return The concatenated prefix and user key value.
 */
internal fun generateKey(
  prefix: String,
  userKey: String,
): String {
  val generatedKey = if (prefix.endsWith(":")) prefix + userKey else "$prefix:$userKey"
  logger.debug { "Generated key = $generatedKey" }
  return generatedKey.trim { it <= ' ' }
}

/** Service class that provides access operations for the underlying data store. */
@SuppressFBWarnings("EI_EXPOSE_REP2", "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
class DataStoreService(
  private val encryptionUtils: EncryptionUtils,
  private val redisTemplate: RedisTemplate<String, String>,
  private val prefix: String,
) {
  /**
   * Retrieves the value associated with the provided key value.
   *
   * @param key The key value used to lookup data in the data store.
   * @return An [Optional] that may contain the value associated with the requested key.
   * @throws SecurityException if unable to decrypt the stored value.
   */
  @Throws(SecurityException::class)
  fun getValue(key: String): Optional<String> {
    logger.debug { "Attempting to retrieve value stored at key '$key'..." }
    val value = redisTemplate.opsForValue().get(generateKey(prefix, key))
    if (!value.isNullOrBlank()) {
      logger.debug { "Retrieved value '$value' associated with key '$key'." }
      return Optional.of(encryptionUtils.decrypt(value))
    } else {
      logger.debug { "Could not find any values associated with key '$key'." }
      return Optional.empty<String>()
    }
  }

  /**
   * Sets the value of the provided key in the data store.
   *
   * @param key The key.
   * @param value The value.
   * @throws SecurityException if unable to encrypt the value prior to storage.
   */
  @Throws(SecurityException::class)
  fun setValue(
    key: String,
    value: String?,
  ) {
    logger.debug { "Attempting to save a value to key '$key'..." }
    redisTemplate.opsForValue().set(generateKey(prefix, key), encryptionUtils.encrypt(value))
    logger.info { "Successfully set the value of key '$key'." }
  }
}
