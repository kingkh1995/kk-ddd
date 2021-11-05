package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import com.kkk.op.support.marker.EntityCache.ValueWrapper;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 * 可缓存Repository marker（缓存null值）
 *
 * @author KaiKoo
 */
public interface CacheableRepository<T extends Entity<ID>, ID extends Identifier> {

  boolean isAutoCaching();

  EntityCache getCache();

  String generateCacheKey(@NotNull ID id);

  Optional<ValueWrapper<T>> cacheGetIfPresent(@NotNull ID id);

  Optional<T> cacheGet(@NotNull ID id);

  void cachePut(@NotNull T t);

  boolean cachePutIfAbsent(@NotNull T t);

  void cacheRemove(@NotNull ID id);

  void cacheDelayRemove(@NotNull ID id);

  default void cacheDoubleRemove(@NotNull ID id, Runnable runnable) {
    this.cacheRemove(id);
    runnable.run();
    this.cacheDelayRemove(id);
  }
}
