package com.kkk.op.support.cache;

import com.kkk.op.support.marker.Cache;
import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.util.Assert;

/**
 * 二级缓存 （local+external） <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class TwoStageCache implements Cache {

  private final String name;
  private final LocalCache localCache;
  private final RedisCache redisCache;
  private final RTopic topic;

  public TwoStageCache(LocalCache localCache, RedisCache redisCache) {
    Assert.notNull(localCache, "Is null!");
    Assert.notNull(redisCache, "Is null!");
    this.localCache = localCache;
    this.redisCache = redisCache;
    this.name = String.format("TwoStageCache(%s)(%s)", localCache.getName(), redisCache.getName());
    var topic = redisCache.getRedissonClient().getTopic(this.name + "-Topic");
    // evict消息监听器
    topic.addListener(
        String.class,
        (channel, msg) -> {
          log.info("Receive evict message from '{}', evict key '{}'.", channel, msg);
          localCache.evict(msg);
        });
    // clear消息监听器
    topic.addListener(
        ClearMessage.class,
        (channel, msg) -> {
          log.info("Receive clear message from '{}'.", channel);
          localCache.clear();
        });
    this.topic = topic;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public <T> Optional<ValueWrapper<T>> get(String key, Class<T> clazz) {
    // 先在local中查找
    var op = localCache.get(key, clazz);
    if (op.isPresent()) {
      return op;
    }
    // 再到redis中查找，如果缓存命中，拉取到local，拉取允许失败
    op = redisCache.get(key, clazz);
    try {
      op.ifPresent(wrapper -> localCache.put(key, wrapper.get()));
    } catch (Exception e) {
      log.warn("Pull cache from redis to local error!", e);
    }
    return op;
  }

  @Override
  public void put(String key, Object obj) {
    // 先put到redis，不允许失败
    redisCache.put(key, obj);
    // 再put到local，允许失败
    try {
      localCache.put(key, obj);
    } catch (Exception e) {
      log.warn("Push to local error!", e);
    }
  }

  @Override
  public void evict(String key) {
    redisCache.evict(key);
    // 发布消息，触发各服务器节点localCache失效
    topic.publish(key);
  }

  @Override
  public void clear() {
    redisCache.clear();
    // 发布消息，触发各服务器节点localCache清空
    topic.publish(ClearMessage.INSTANCE);
  }

  private static final class ClearMessage implements Serializable {

    static final Object INSTANCE = new ClearMessage();

    static final long serialVersionUID = 1L;

    // 添加readResolve，反序列化返回INSTANCE
    @Serial
    private Object readResolve() {
      return INSTANCE;
    }
  }
}
