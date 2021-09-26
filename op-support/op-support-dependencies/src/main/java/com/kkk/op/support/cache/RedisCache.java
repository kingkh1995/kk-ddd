package com.kkk.op.support.cache;

import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.marker.Cache;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.util.Assert;

/** @author KaiKoo */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisCache implements Cache {

  @Getter private final RedissonClient redissonClient;
  private final RedissonCache redissonCache;
  private final Kson kson;

  public static RedisCacheBuilder builder() {
    return new RedisCacheBuilder();
  }

  @Override
  public String getName() {
    return redissonCache.getName();
  }

  @Override
  public <T> Optional<ValueWrapper<T>> get(String key, Class<T> clazz) {
    // redissonCache get return null if not exist
    var valueWrapper = redissonCache.get(key);
    if (valueWrapper == null) {
      return Optional.empty();
    } else if (valueWrapper == org.redisson.spring.cache.NullValue.INSTANCE) {
      return Optional.of(NullValue.instance());
    }
    return Optional.of(new SimpleValue<>(kson.readJson((String) valueWrapper.get(), clazz)));
  }

  @Override
  public void put(String key, Object obj) {
    redissonCache.put(key, kson.writeJson(obj));
  }

  @Override
  public void evict(String key) {
    redissonCache.evict(key);
  }

  @Override
  public void clear() {
    redissonCache.clear();
  }

  @Setter
  @Accessors(fluent = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class RedisCacheBuilder {

    private String name;
    private RedissonClient redissonClient;
    private CacheConfig redissonCacheConfig;
    private Kson kson;

    public RedisCache build() {
      Assert.hasText(this.name, "Is empty!");
      Assert.notNull(this.redissonClient, "Is null!");
      Assert.notNull(this.redissonCacheConfig, "Is null!");
      Assert.notNull(this.kson, "Is null!");
      // always allow null values
      var redissonCache =
          new RedissonCache(this.redissonClient.getMapCache(name), this.redissonCacheConfig, true);
      return new RedisCache(this.redissonClient, redissonCache, this.kson);
    }
  }
}
