package com.kkk.op.support.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.util.Assert;

/** @author KaiKoo */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisCache extends SpringCacheWrapper {

  @Getter private final RedissonClient redissonClient;
  @Getter private final RedissonCache cache;

  public static RedisCacheBuilder builder() {
    return new RedisCacheBuilder();
  }

  @Setter
  @Accessors(fluent = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class RedisCacheBuilder {

    private String name;
    private RedissonClient redissonClient;
    private CacheConfig redissonCacheConfig;
    private ObjectMapper objectMapper;

    public RedisCache build() {
      Assert.hasText(this.name, "Is empty!");
      Assert.notNull(this.redissonClient, "Is null!");
      Assert.notNull(this.redissonCacheConfig, "Is null!");
      Assert.notNull(this.objectMapper, "Is null!");
      // use jackson json & always allow null values
      var redissonCache =
          new RedissonCache(
              this.redissonClient.getMapCache(name, new JsonJacksonCodec(this.objectMapper)),
              this.redissonCacheConfig,
              true);
      return new RedisCache(this.redissonClient, redissonCache);
    }
  }
}
