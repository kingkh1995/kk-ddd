package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 * 可缓存Repository marker
 *
 * @author KaiKoo
 */
public interface CacheableRepository<T extends Entity<ID>, ID extends Identifier> {

  void cachePut(@NotNull T t);

  Optional<T> cacheGet(@NotNull ID id);

  boolean cacheRemove(@NotNull T t);

  String generateCacheKey(@NotNull ID id);

  // todo... 缓存双删 通过EventBus发送消息?
  default void cacheDoubleRemove(@NotNull T t, Runnable runnable) {
    this.cacheRemove(t);
    runnable.run();
    // 延迟删除
  }
}
