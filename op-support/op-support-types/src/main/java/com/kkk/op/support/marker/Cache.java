package com.kkk.op.support.marker;

import java.util.Optional;
import java.util.concurrent.Callable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

/**
 * todo... 服务降级、设计优化
 *
 * @see org.springframework.cache.Cache
 * @author KaiKoo
 */
public interface Cache {

  String getName();

  <T> Optional<ValueWrapper<T>> get(@NotBlank String key, @NotNull Class<T> type);

  <T> Optional<T> get(@NotBlank String key, @NotNull Class<T> type, @NotNull Callable<T> loader);

  void put(@NotBlank String key, @NotNull Object obj);

  void evict(@NotBlank String key);

  void clear();

  interface ValueWrapper<T> {
    @Nullable
    T get();
  }
}
