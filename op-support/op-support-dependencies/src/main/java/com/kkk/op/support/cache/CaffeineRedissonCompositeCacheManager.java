package com.kkk.op.support.cache;

import com.kkk.op.support.bean.JsonJacksonCoder;
import java.util.Collection;
import java.util.Optional;
import lombok.SneakyThrows;
import org.redisson.api.RTopic;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class CaffeineRedissonCompositeCacheManager extends TwoStageCacheManager
    implements ResourceLoaderAware {

  private final RedissonSpringCacheManager redissonCacheManager;
  private final CaffeineCacheManager caffeineCacheManager;
  private final RTopic rTopic;
  private final JsonJacksonCoder jsonJacksonCoder;

  public CaffeineRedissonCompositeCacheManager(
      boolean allowNullValues,
      RedissonSpringCacheManager redissonCacheManager,
      CaffeineCacheManager caffeineCacheManager,
      RTopic rTopic,
      JsonJacksonCoder jsonJacksonCoder) {
    this.redissonCacheManager = redissonCacheManager;
    this.caffeineCacheManager = caffeineCacheManager;
    this.redissonCacheManager.setAllowNullValues(allowNullValues);
    this.caffeineCacheManager.setAllowNullValues(allowNullValues);
    // 添加evict消息监听器
    rTopic.addListener(
        TwoStageCacheEvictMessage.class,
        (channel, msg) ->
            Optional.ofNullable(this.caffeineCacheManager.getCache(msg.getName()))
                .ifPresent(
                    cache -> {
                      if (msg.getKey() == null) {
                        cache.clear();
                      } else {
                        cache.evict(msg.getKey());
                      }
                    }));
    this.rTopic = rTopic;
    this.jsonJacksonCoder = jsonJacksonCoder;
  }

  public CaffeineRedissonCompositeCacheManager(
      RedissonSpringCacheManager redissonCacheManager,
      CaffeineCacheManager caffeineCacheManager,
      RTopic rTopic,
      JsonJacksonCoder jsonJacksonCoder) {
    this(true, redissonCacheManager, caffeineCacheManager, rTopic, jsonJacksonCoder);
  }

  @Override
  public void publishMessage(TwoStageCacheEvictMessage message) {
    this.rTopic.publish(message);
  }

  @Override
  public Cache getCache(String name) {
    var redissonCache = this.redissonCacheManager.getCache(name);
    var caffeineCache = this.caffeineCacheManager.getCache(name);
    if (redissonCache == null || caffeineCache == null) {
      return null;
    }
    return new TransactionAwareCacheDecorator(
        new TwoStageCache(
            this,
            redissonCache,
            new SafeLocalCacheDecorator(caffeineCache, this.jsonJacksonCoder)));
  }

  @Override
  public Collection<String> getCacheNames() {
    return this.redissonCacheManager.getCacheNames();
  }

  @SneakyThrows
  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    // 因为redissonCacheManager未被spring容器管理，需要手动执行一下afterPropertiesSet方法。
    this.redissonCacheManager.setResourceLoader(resourceLoader);
    this.redissonCacheManager.afterPropertiesSet();
  }
}
