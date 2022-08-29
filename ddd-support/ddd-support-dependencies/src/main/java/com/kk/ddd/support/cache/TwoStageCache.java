package com.kk.ddd.support.cache;

import com.kk.ddd.support.cache.TwoStageCacheManager.TwoStageCacheEvictMessage;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;

/**
 * 使用复合方式实现二级缓存 （local + global） <br>
 *
 * @author KaiKoo
 */
@RequiredArgsConstructor
public class TwoStageCache implements Cache {

  private final TwoStageCacheManager cacheManager;
  private final Cache globalCache;
  private final Cache localCache;

  @Override
  public String getName() {
    return globalCache.getName();
  }

  @Override
  public Object getNativeCache() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ValueWrapper get(Object key) {
    // 先在local中查找
    var valueWrapper = localCache.get(key);
    if (valueWrapper != null) {
      return valueWrapper;
    }
    // 再到global中查找，如果缓存命中，拉取到local。
    valueWrapper = globalCache.get(key);
    if (valueWrapper != null) {
      localCache.put(key, valueWrapper.get());
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
    return localCache.get(key, () -> globalCache.get(key, valueLoader));
  }

  @Override
  public void put(Object key, Object value) {
    // put到global
    globalCache.put(key, value);
    // 发布缓存淘汰消息
    publishEvictMessage(key);
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    // putIfAbsent到global，成功则发布缓存淘汰消息。
    var existingValue = globalCache.putIfAbsent(key, value);
    if (existingValue == null) {
      publishEvictMessage(key);
    }
    return existingValue;
  }

  @Override
  public void evict(Object key) {
    globalCache.evict(key);
    // 发布消息，触发所有服务器节点localCache缓存淘汰。
    publishEvictMessage(key);
  }

  @Override
  public void clear() {
    globalCache.clear();
    // 发布消息，触发所有服务器节点localCache缓存清空。
    publishClearMessage();
  }

  private void publishEvictMessage(Object key) {
    cacheManager.publishMessage(
        TwoStageCacheEvictMessage.builder().name(getName()).key(key).build());
  }

  private void publishClearMessage() {
    cacheManager.publishMessage(TwoStageCacheEvictMessage.builder().name(getName()).build());
  }
}
