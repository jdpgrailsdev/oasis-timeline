package com.jdpgrailsdev.oasis.timeline.config;

import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.jdpgrailsdev.oasis.timeline.util.EncryptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/** Spring configuration for data store related beans. */
@Configuration
public class DataStoreConfiguration {

  /**
   * Spring Data {@link RedisTemplate} bean used to access a Redis database.
   *
   * @param redisConnectionFactory The {@link RedisConnectionFactory}.
   * @return The {@link RedisTemplate} bean.
   */
  @Bean
  public RedisTemplate<String, String> redisTemplate(
      final RedisConnectionFactory redisConnectionFactory) {
    final RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }

  /**
   * Encryption utility that provides methods for encrypting/decrypting a value.
   *
   * @param key The encryption key.
   * @param transformation The encryption transformation
   * @return The {@link EncryptionUtils} bean.
   */
  @Bean
  public EncryptionUtils encryptionUtils(
      @Value("${spring.data.redis.security.key}") final String key,
      @Value("${spring.data.redis.security.transformation}") final String transformation) {
    return new EncryptionUtils(key, transformation);
  }

  /**
   * Service bean that provides access to a data store.
   *
   * @param encryptionUtils The encryption utility.
   * @param redisTemplate The {@link RedisTemplate} for database access.
   * @param prefix The key prefix for all data stored/retrieved in/from the data store.
   * @return The {@link DataStoreService} bean.
   */
  @Bean
  public DataStoreService dataStoreService(
      final EncryptionUtils encryptionUtils,
      final RedisTemplate<String, String> redisTemplate,
      @Value("${spring.data.redis.prefix}") final String prefix) {
    return new DataStoreService(encryptionUtils, redisTemplate, prefix);
  }
}
