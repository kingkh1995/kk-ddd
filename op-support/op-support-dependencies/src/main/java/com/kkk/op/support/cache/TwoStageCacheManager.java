package com.kkk.op.support.cache;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import lombok.Builder;
import lombok.SneakyThrows;
import org.redisson.api.RTopic;
import org.redisson.spring.cache.RedissonCache;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class TwoStageCacheManager implements CacheManager, ResourceLoaderAware {

  private final RTopic rTopic;
  private final RedissonSpringCacheManager redissonCacheManager;
  private final CaffeineCacheManager caffeineCacheManager;

  public TwoStageCacheManager(
      boolean allowNullValues,
      RTopic rTopic,
      RedissonSpringCacheManager redissonCacheManager,
      CaffeineCacheManager caffeineCacheManager) {
    this.rTopic = rTopic;
    this.redissonCacheManager = redissonCacheManager;
    this.caffeineCacheManager = caffeineCacheManager;
    this.redissonCacheManager.setAllowNullValues(allowNullValues);
    this.caffeineCacheManager.setAllowNullValues(allowNullValues);
    // 添加evict消息监听器
    rTopic.addListener(
        TwoStageCacheEvictMessage.class,
        (channel, msg) ->
            Optional.ofNullable(this.caffeineCacheManager.getCache(msg.name))
                .ifPresent(
                    cache -> {
                      if (msg.key == null) {
                        cache.clear();
                      } else {
                        cache.evict(msg.key);
                      }
                    }));
  }

  public TwoStageCacheManager(
      RTopic rTopic,
      RedissonSpringCacheManager redissonCacheManager,
      CaffeineCacheManager caffeineCacheManager) {
    this(true, rTopic, redissonCacheManager, caffeineCacheManager);
  }

  @Override
  public Cache getCache(String name) {
    var redissonCache = (RedissonCache) redissonCacheManager.getCache(name);
    var caffeineCache = (CaffeineCache) caffeineCacheManager.getCache(name);
    if (redissonCache == null || caffeineCache == null) {
      return null;
    }
    return new TwoStageCache(this.rTopic, redissonCache, caffeineCache);
  }

  @Override
  public Collection<String> getCacheNames() {
    return redissonCacheManager.getCacheNames();
  }

  @SneakyThrows
  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    // 因为redissonCacheManager未被spring容器管理，需要手动执行一下afterPropertiesSet方法。
    this.redissonCacheManager.setResourceLoader(resourceLoader);
    this.redissonCacheManager.afterPropertiesSet();
  }

  @JsonSerialize(typing = JsonSerialize.Typing.DYNAMIC) // 设置动态序列化，输出具体类型。
  @JsonDeserialize(builder = TwoStageCacheEvictMessage.TwoStageCacheEvictMessageBuilder.class)
  @Builder
  public static class TwoStageCacheEvictMessage implements Serializable {
    private String name;
    private Object key;
  }
}
