package com.kkk.op.support.cache;

import com.kkk.op.support.cache.TwoStageCacheManager.TwoStageCacheEvictMessage;
import java.util.concurrent.Callable;
import lombok.AllArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;

/**
 * 二级缓存 （local + redis） <br>
 *
 * @author KaiKoo
 */
@AllArgsConstructor
public class TwoStageCache implements Cache {

  private final RTopic rTopic;
  private final RedissonCache redissonCache;
  private final CaffeineCache caffeineCache;

  @Override
  public String getName() {
    return redissonCache.getName();
  }

  @Override
  public Object getNativeCache() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ValueWrapper get(Object key) {
    // 先在local中查找
    var valueWrapper = caffeineCache.get(key);
    if (valueWrapper != null) {
      return valueWrapper;
    }
    // 再到redis中查找，如果缓存命中，拉取到local。
    valueWrapper = redissonCache.get(key);
    if (valueWrapper != null) {
      caffeineCache.put(key, valueWrapper.get());
    }
    return valueWrapper;
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    var valueWrapper = get(key);
    // 为空表示缓存不存在，直接返回
    if (valueWrapper == null) {
      return null;
    }
    // 非空则判断类型是否匹配
    var value = valueWrapper.get();
    if (value != null && type != null && !type.isInstance(value)) {
      throw new IllegalStateException(
          "Cached value is not of required type [" + type.getName() + "]: " + value);
    }
    return (T) value;
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    return caffeineCache.get(key, () -> redissonCache.get(key, valueLoader));
  }

  @Override
  public void put(Object key, Object value) {
    // put到redis
    redissonCache.put(key, value);
    // 发布缓存淘汰消息
    publishEvictMessage(key);
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    // putIfAbsent到redis，成功则发布缓存淘汰消息。
    var existingValue = redissonCache.putIfAbsent(key, value);
    if (existingValue == null) {
      publishEvictMessage(key);
    }
    return existingValue;
  }

  @Override
  public void evict(Object key) {
    redissonCache.evict(key);
    // 发布消息，触发所有服务器节点localCache缓存淘汰。
    publishEvictMessage(key);
  }

  @Override
  public void clear() {
    redissonCache.clear();
    // 发布消息，触发所有服务器节点localCache缓存清空。
    publishClearMessage();
  }

  private void publishEvictMessage(Object key) {
    rTopic.publish(TwoStageCacheEvictMessage.builder().name(getName()).key(key).build());
  }

  private void publishClearMessage() {
    rTopic.publish(TwoStageCacheEvictMessage.builder().name(getName()).build());
  }
}
