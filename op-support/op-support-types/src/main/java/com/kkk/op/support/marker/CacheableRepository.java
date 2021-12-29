package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import com.kkk.op.support.marker.Cache.ValueWrapper;
import java.util.Optional;
import java.util.function.Consumer;
import javax.validation.constraints.NotNull;

/**
 * 可缓存Repository marker（缓存null值）
 *
 * @author KaiKoo
 */
public interface CacheableRepository<T extends Entity<ID>, ID extends Identifier> {

  boolean isAutoCaching();

  Cache getCache();

  String generateCacheKey(@NotNull ID id);

  Optional<ValueWrapper<T>> cacheGetIfPresent(@NotNull ID id);

  Optional<T> cacheGet(@NotNull ID id);

  void cachePut(@NotNull T t);

  boolean cachePutIfAbsent(@NotNull T t);

  void cacheRemove(@NotNull ID id);

  void cacheDelayRemoveAsync(@NotNull ID id);

  default Consumer<? super T> cacheDoubleRemoveWrap(
      boolean isAutoCaching, Consumer<? super T> consumer) {
    if (!isAutoCaching) {
      return consumer;
    }
    return t -> {
      this.cacheRemove(t.getId());
      consumer.accept(t);
      this.cacheDelayRemoveAsync(t.getId());
    };
  }
}
