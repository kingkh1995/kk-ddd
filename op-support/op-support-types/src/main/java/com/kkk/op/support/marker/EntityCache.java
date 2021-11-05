package com.kkk.op.support.marker;

import java.util.Optional;
import java.util.concurrent.Callable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

/**
 * 缓存接口
 *
 * @see org.springframework.cache.Cache
 * @author KaiKoo
 */
public interface EntityCache {

  String getName();

  <T> Optional<ValueWrapper<T>> get(@NotBlank String key, @NotNull Class<T> type);

  <T> Optional<T> get(@NotBlank String key, @NotNull Callable<T> loader);

  void put(@NotBlank String key, @NotNull Object obj);

  boolean putIfAbsent(@NotBlank String key, @Nullable Object obj);

  void evict(@NotBlank String key);

  void clear();

  interface ValueWrapper<T> {
    @Nullable
    T get();
  }
}
