package com.kkk.op.support.cache;

import com.kkk.op.support.json.JsonJacksonCoder;
import java.util.concurrent.Callable;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

/**
 * 安全的本地缓存，转为Json存储，保证了缓存不会修改。 <br>
 *
 * @author KaiKoo
 */
public class SafeLocalCacheDecorator implements Cache {

  private final Cache targetCache;

  private final JsonJacksonCoder jsonJacksonCoder;

  public SafeLocalCacheDecorator(Cache targetCache, JsonJacksonCoder jsonJacksonCoder) {
    Assert.notNull(targetCache, "Target Cache must not be null");
    Assert.notNull(jsonJacksonCoder, "Coder must not be null");
    this.targetCache = targetCache;
    this.jsonJacksonCoder = jsonJacksonCoder;
  }

  public Cache getTargetCache() {
    return this.targetCache;
  }

  @Override
  public String getName() {
    return this.targetCache.getName();
  }

  @Override
  public Object getNativeCache() {
    return this.targetCache.getNativeCache();
  }

  @Override
  public ValueWrapper get(Object key) {
    var valueWrapper = this.targetCache.get(key);
    return null == valueWrapper
        ? null
        : new SimpleValueWrapper(this.jsonJacksonCoder.decode((String) valueWrapper.get()));
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    return (T) this.jsonJacksonCoder.decode(this.targetCache.get(key, String.class));
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    return (T)
        this.jsonJacksonCoder.decode(
            this.targetCache.get(key, () -> this.jsonJacksonCoder.encode(valueLoader.call())));
  }

  @Override
  public void put(Object key, Object value) {
    this.targetCache.put(key, this.jsonJacksonCoder.encode(value));
  }

  @Override
  public void evict(Object key) {
    this.targetCache.evict(key);
  }

  @Override
  public void clear() {
    this.targetCache.clear();
  }
}
