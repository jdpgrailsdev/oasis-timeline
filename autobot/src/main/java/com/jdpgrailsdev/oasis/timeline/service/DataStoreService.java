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

package com.jdpgrailsdev.oasis.timeline.service;

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import com.jdpgrailsdev.oasis.timeline.util.EncryptionUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

/** Service class that provides access operations for the underlying data store. */
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class DataStoreService {

  private static final Logger log = LoggerFactory.getLogger(DataStoreService.class);

  private final EncryptionUtils encryptionUtils;
  private final String prefix;
  private final RedisTemplate<String, String> redisTemplate;

  public DataStoreService(
      final EncryptionUtils encryptionUtils,
      final RedisTemplate<String, String> redisTemplate,
      final String prefix) {
    this.encryptionUtils = encryptionUtils;
    this.prefix = prefix;
    this.redisTemplate = redisTemplate;
  }

  /**
   * Retrieves the value associated with the provided key value.
   *
   * @param key The key value used to lookup data in the data store.
   * @return An {@link Optional} that may contain the value associated with the requested key.
   * @throws SecurityException if unable to decrypt the stored value.
   */
  public Optional<String> getValue(final String key) throws SecurityException {
    log.debug("Attempting to retrieve value stored at key '{}'...", key);
    final String value = redisTemplate.opsForValue().get(generateKey(prefix, key));
    if (StringUtils.hasText(value)) {
      log.debug("Retrieved value '{}' associated with key '{}'.", value, key);
      return Optional.of(encryptionUtils.decrypt(value));
    } else {
      log.debug("Could not find any values associated with key '{}'.", key);
      return Optional.empty();
    }
  }

  /**
   * Sets the value of the provided key in the data store.
   *
   * @param key The key.
   * @param value The value.
   * @throws SecurityException if unable to encrypt the value prior to storage.
   */
  public void setValue(final String key, final String value) throws SecurityException {
    log.debug("Attempting to save a value to key '{}'...", key);
    redisTemplate.opsForValue().set(generateKey(prefix, key), encryptionUtils.encrypt(value));
    log.info("Successfully set the value of key '{}'.", key);
  }

  /**
   * Generates the data store key based on the provided prefix and user key.
   *
   * @param prefix The configured prefix for all entries in the data store.
   * @param userKey The user provided key value.
   * @return The concatenated prefix and user key value.
   */
  public static String generateKey(final String prefix, final String userKey) {
    final String generatedKey = prefix.endsWith(":") ? prefix + userKey : prefix + ":" + userKey;
    log.debug("Generated key = {}", generatedKey);
    return generatedKey.trim();
  }
}
