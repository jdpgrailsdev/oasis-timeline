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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import com.jdpgrailsdev.oasis.timeline.util.EncryptionUtils;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/** Test suite for the {@link DataStoreService class}. */
class DataStoreServiceTests {

  private static final String KEY = "key";
  private static final String PREFIX = "redis:prefix:";
  private static final String VALUE = "a value";

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void testRetrieveValueFromDataStore() throws SecurityException {
    final String key = KEY;
    final String value = VALUE;
    final EncryptionUtils encryptionUtils = mock(EncryptionUtils.class);
    final RedisTemplate redisTemplate = mock(RedisTemplate.class);
    final ValueOperations valueOperations = mock(ValueOperations.class);

    when(encryptionUtils.decrypt(value)).thenReturn(value);
    when(valueOperations.get(any())).thenReturn(value);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    final DataStoreService service = new DataStoreService(encryptionUtils, redisTemplate, PREFIX);

    final Optional<String> actualValue = service.getValue(key);
    assertTrue(actualValue.isPresent());
    assertEquals(value, actualValue.get());
    verify(valueOperations, times(1)).get(DataStoreService.generateKey(PREFIX, key));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void testRetrieveValueFromDataStoreMissing() throws SecurityException {
    final String key = KEY;
    final EncryptionUtils encryptionUtils = mock(EncryptionUtils.class);
    final RedisTemplate redisTemplate = mock(RedisTemplate.class);
    final ValueOperations valueOperations = mock(ValueOperations.class);

    when(valueOperations.get(any())).thenReturn(null);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    final DataStoreService service = new DataStoreService(encryptionUtils, redisTemplate, PREFIX);

    final Optional<String> actualValue = service.getValue(key);
    assertTrue(actualValue.isEmpty());
    verify(valueOperations, times(1)).get(DataStoreService.generateKey(PREFIX, key));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void testRetrieveValueFromDataStoreDecryptionFailure() throws SecurityException {
    final String key = KEY;
    final String value = VALUE;
    final EncryptionUtils encryptionUtils = mock(EncryptionUtils.class);
    final RedisTemplate redisTemplate = mock(RedisTemplate.class);
    final ValueOperations valueOperations = mock(ValueOperations.class);

    when(encryptionUtils.decrypt(value)).thenThrow(SecurityException.class);
    when(valueOperations.get(any())).thenReturn(value);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    final DataStoreService service = new DataStoreService(encryptionUtils, redisTemplate, PREFIX);

    assertThrows(SecurityException.class, () -> service.getValue(key));
    verify(valueOperations, times(1)).get(DataStoreService.generateKey(PREFIX, key));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void testSaveValueToDataStore() throws SecurityException {
    final String key = KEY;
    final String value = VALUE;
    final EncryptionUtils encryptionUtils = mock(EncryptionUtils.class);
    final RedisTemplate redisTemplate = mock(RedisTemplate.class);
    final ValueOperations valueOperations = mock(ValueOperations.class);

    when(encryptionUtils.encrypt(value)).thenReturn(value);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    final DataStoreService service = new DataStoreService(encryptionUtils, redisTemplate, PREFIX);

    assertDoesNotThrow(() -> service.setValue(key, value));
    verify(valueOperations, times(1)).set(DataStoreService.generateKey(PREFIX, key), value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void testSaveValueToDataStoreEncryptionFailure() throws SecurityException {
    final String key = KEY;
    final String value = VALUE;
    final EncryptionUtils encryptionUtils = mock(EncryptionUtils.class);
    final RedisTemplate redisTemplate = mock(RedisTemplate.class);
    final ValueOperations valueOperations = mock(ValueOperations.class);

    when(encryptionUtils.encrypt(value)).thenThrow(SecurityException.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    final DataStoreService service = new DataStoreService(encryptionUtils, redisTemplate, PREFIX);

    assertThrows(SecurityException.class, () -> service.setValue(key, value));
    verify(valueOperations, times(0)).set(DataStoreService.generateKey(PREFIX, key), value);
  }

  @Test
  void testKeyGeneration() {
    final String key = KEY;
    assertEquals(PREFIX + key, DataStoreService.generateKey(PREFIX, key));
    assertEquals(
        PREFIX + key, DataStoreService.generateKey(PREFIX.substring(0, PREFIX.length() - 1), key));
  }
}
