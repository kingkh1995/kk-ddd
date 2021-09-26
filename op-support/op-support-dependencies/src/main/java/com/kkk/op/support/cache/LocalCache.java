package com.kkk.op.support.cache;

import com.kkk.op.support.marker.Cache;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.util.Assert;

/**
 * local cache using caffeine <br>
 *
 * @author KaiKoo
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalCache implements Cache {

  private final CaffeineCache cache;

  public static LocalCacheBuilder builder() {
    return new LocalCacheBuilder();
  }

  @Override
  public String getName() {
    return cache.getName();
  }

  @Override
  public <T> Optional<ValueWrapper<T>> get(String key, Class<T> clazz) {
    var storeValue = cache.getNativeCache().getIfPresent(key);
    if (storeValue == null) {
      return Optional.empty();
    } else if (storeValue == org.springframework.cache.support.NullValue.INSTANCE) {
      return Optional.of(NullValue.instance());
    }
    return Optional.of(new SimpleValue<>((T) storeValue));
  }

  @Override
  public void put(String key, Object obj) {
    cache.put(key, obj);
  }

  @Override
  public void evict(String key) {
    cache.evict(key);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Setter
  @Accessors(fluent = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class LocalCacheBuilder {
    private String name;
    private com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache;

    public LocalCache build() {
      Assert.hasText(this.name, "Is empty!");
      Assert.notNull(this.caffeineCache, "Is null!");
      // always allow null values
      var caffeineCache = new CaffeineCache(this.name, this.caffeineCache, true);
      return new LocalCache(caffeineCache);
    }
  }
}
