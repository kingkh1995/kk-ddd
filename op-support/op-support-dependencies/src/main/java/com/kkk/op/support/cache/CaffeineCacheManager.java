package com.kkk.op.support.cache;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.kkk.op.support.marker.CacheManager;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * caffeine cache 支持同步缓存或异步缓存（<br>
 * CacheManager设计不需要异步缓存故废除）
 *
 * @author KaiKoo
 */
@Slf4j
@Deprecated
public class CaffeineCacheManager implements CacheManager {

  private final SyncCaffeine syncCaffeine;

  private final AsyncCaffeine asyncCaffeine;

  private final boolean sync;

  private CaffeineCacheManager(
      SyncCaffeine syncCaffeine, AsyncCaffeine asyncCaffeine, boolean sync) {
    this.syncCaffeine = syncCaffeine;
    this.asyncCaffeine = asyncCaffeine;
    this.sync = sync;
  }

  public static SyncCaffeineBuilder newBuilder() {
    return new SyncCaffeineBuilder();
  }

  @Override
  public void put(String key, Object obj) {
    if (sync) {
      syncCaffeine.put(key, obj);
    } else {
      asyncCaffeine.put(key, obj);
    }
  }

  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    if (sync) {
      return syncCaffeine.get(key, clazz);
    } else {
      return asyncCaffeine.get(key, clazz);
    }
  }

  @Override
  public boolean remove(String key) {
    if (sync) {
      return syncCaffeine.remove(key);
    } else {
      return asyncCaffeine.remove(key);
    }
  }

  @Deprecated
  static class SyncCaffeine implements CacheManager {

    private final Cache<String, Object> cache;

    public SyncCaffeine(Cache<String, Object> cache) {
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

  @Deprecated
  static class AsyncCaffeine implements CacheManager {

    private final AsyncCache<String, Object> cache;

    public AsyncCaffeine(AsyncCache<String, Object> cache) {
      this.cache = Objects.requireNonNull(cache);
    }

    @Override
    public void put(String key, Object obj) {
      cache.put(key, CompletableFuture.completedFuture(obj));
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
      return Optional.ofNullable(cache.getIfPresent(key))
          .map(
              future -> {
                try {
                  return (T) future.get();
                } catch (Throwable e) {
                  log.warn("Async cache get failed!", e);
                  return null;
                }
              });
    }

    @Override
    public boolean remove(String key) {
      cache.put(key, CompletableFuture.completedFuture(null));
      return true;
    }
  }

  public static class SyncCaffeineBuilder {

    private Cache<String, Object> cache;

    public SyncCaffeineBuilder cache(Cache<String, Object> cache) {
      this.cache = cache;
      return this;
    }

    public CaffeineCacheManager build() {
      return new CaffeineCacheManager(new SyncCaffeine(this.cache), null, true);
    }

    public AsyncCaffeineBuilder async() {
      return new AsyncCaffeineBuilder();
    }
  }

  public static class AsyncCaffeineBuilder {

    private AsyncCache<String, Object> cache;

    public AsyncCaffeineBuilder cache(AsyncCache<String, Object> cache) {
      this.cache = cache;
      return this;
    }

    public CaffeineCacheManager build() {
      return new CaffeineCacheManager(null, new AsyncCaffeine(this.cache), false);
    }

    public SyncCaffeineBuilder sync() {
      var builder = new SyncCaffeineBuilder();
      if (this.cache != null) {
        builder.cache(this.cache.synchronous());
      }
      return builder;
    }
  }
}
