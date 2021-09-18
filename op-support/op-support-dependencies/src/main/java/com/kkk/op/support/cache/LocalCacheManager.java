package com.kkk.op.support.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.kkk.op.support.marker.CacheManager;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;

/**
 * local cache using caffeine <br>
 *
 * @author KaiKoo
 */
@Builder
public class LocalCacheManager implements CacheManager {

  private final Cache<String, Object> cache;

  private LocalCacheManager(Cache<String, Object> cache) {
    this.cache = Objects.requireNonNull(cache);
  }

  @Override
  public void put(String key, Object obj) {
    cache.put(key, obj);
  }

  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    return Optional.ofNullable((T) cache.getIfPresent(key));
  }

  @Override
  public boolean remove(String key) {
    cache.invalidate(key);
    return true;
  }
}
