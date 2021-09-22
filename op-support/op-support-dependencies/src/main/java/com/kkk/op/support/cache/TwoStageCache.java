package com.kkk.op.support.cache;

import com.kkk.op.support.marker.Cache;
import com.kkk.op.support.marker.ValueWrapper;
import java.util.Optional;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.util.Assert;

/**
 * 二级缓存 （local+external） <br>
 *
 * @author KaiKoo
 */
public class TwoStageCache implements Cache {

  // todo... 添加key名称校验
  private static final String CLEAR_MESSAGE = "*";

  private final String name;
  private final LocalCache localCache;
  private final RedisCache redisCache;
  private final RTopic topic;

  public TwoStageCache(
      LocalCache localCache, RedisCache redisCache, RedissonClient redissonClient) {
    Assert.notNull(localCache, "Is null!");
    Assert.notNull(redisCache, "Is null!");
    this.localCache = localCache;
    this.redisCache = redisCache;
    this.name = String.format("TwoStage-%s-%s", localCache.getName(), redisCache.getName());
    var topic = redissonClient.getTopic(this.name + "-Topic");
    topic.addListener(
        String.class,
        (channel, msg) -> {
          if (CLEAR_MESSAGE.equals(msg)) {
            localCache.clear();
          } else {
            localCache.evict(msg);
          }
        });
    this.topic = topic;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public <T> Optional<ValueWrapper<T>> get(String key, Class<T> clazz) {
    var op = localCache.get(key, clazz);
    if (op.isPresent()) {
      return op;
    }
    op = redisCache.get(key, clazz);
    // 如果redisCache缓存命中，拉取到localCache
    op.ifPresent(tValueWrapper -> localCache.put(key, tValueWrapper.get()));
    return op;
  }

  @Override
  public void put(String key, Object obj) {
    localCache.put(key, obj);
    redisCache.put(key, obj);
  }

  @Override
  public void evict(String key) {
    localCache.evict(key);
    redisCache.evict(key);
    // 发布消息，使其他服务器节点的localCache失效
    topic.publish(key);
  }

  @Override
  public void clear() {
    localCache.clear();
    redisCache.clear();
    // 发布消息，清空其他服务器节点的localCache失
    topic.publish(CLEAR_MESSAGE);
  }
}
