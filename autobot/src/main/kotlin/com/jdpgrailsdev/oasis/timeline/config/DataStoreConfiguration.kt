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

import com.jdpgrailsdev.oasis.timeline.service.DataStoreService
import com.jdpgrailsdev.oasis.timeline.util.EncryptionUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

/** Spring configuration for data store related beans. */
@Suppress("UNUSED")
@Configuration
class DataStoreConfiguration {

  /**
   * Spring Data [RedisTemplate] bean used to access a Redis database.
   *
   * @param redisConnectionFactory The [RedisConnectionFactory].
   * @return The [RedisTemplate] bean.
   */
  @Bean
  fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
    val redisTemplate = RedisTemplate<String, String>()
    redisTemplate.connectionFactory = redisConnectionFactory
    return redisTemplate
  }

  /**
   * Encryption utility that provides methods for encrypting/decrypting a value.
   *
   * @param key The encryption key.
   * @param transformation The encryption transformation
   * @return The [EncryptionUtils] bean.
   */
  @Bean
  fun encryptionUtils(
    @Value($$"${spring.data.redis.security.key}") key: String,
    @Value($$"${spring.data.redis.security.transformation}") transformation: String,
  ): EncryptionUtils = EncryptionUtils(key, transformation)

  /**
   * Service bean that provides access to a data store.
   *
   * @param encryptionUtils The encryption utility.
   * @param redisTemplate The [RedisTemplate] for database access.
   * @param prefix The key prefix for all data stored/retrieved in/from the data store.
   * @return The [DataStoreService] bean.
   */
  @Bean
  fun dataStoreService(
    encryptionUtils: EncryptionUtils,
    redisTemplate: RedisTemplate<String, String>,
    @Value($$"${spring.data.redis.prefix}") prefix: String,
  ): DataStoreService = DataStoreService(encryptionUtils, redisTemplate, prefix)
}
