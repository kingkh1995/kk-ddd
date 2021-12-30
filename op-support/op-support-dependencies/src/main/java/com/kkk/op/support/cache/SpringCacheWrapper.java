package com.kkk.op.support.cache;

import com.kkk.op.support.marker.Cache;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * 封装 spring cache <br>
 *
 * @author KaiKoo
 */
public abstract class SpringCacheWrapper implements Cache {

  public abstract org.springframework.cache.Cache getCache();

  @Override
  public String getName() {
    return getCache().getName();
  }

  @Override
  public <T> Optional<ValueWrapper<T>> get(String key, Class<T> type) {
    var valueWrapper = getCache().get(key);
    if (valueWrapper == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(valueWrapper.get())
        .map(
            value -> {
              if (type != null && !type.isInstance(value)) {
                throw new IllegalStateException(
                    "Cached value is not of required type [" + type.getName() + "]: " + value);
              }
              return (T) value;
            })
        .map(SimpleValue::from)
        .or(() -> Optional.of(NullValue.instance()));
  }

  @Override
  public <T> Optional<T> get(String key, Callable<T> loader) {
    return Optional.ofNullable(getCache().get(key, loader));
  }

  @Override
  public void put(String key, Object obj) {
    getCache().put(key, obj);
  }

  @Override
  public boolean putIfAbsent(String key, Object obj) {
    return getCache().putIfAbsent(key, obj) == null;
  }

  @Override
  public void evict(String key) {
    getCache().evict(key);
  }

  @Override
  public void clear() {
    getCache().clear();
  }
}