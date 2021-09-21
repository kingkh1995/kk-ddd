package com.kkk.op.support.marker;

import java.util.Optional;
import java.util.function.Supplier;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

/**
 * todo... 服务降级、设计优化
 *
 * @author KaiKoo
 */
public interface CacheManager {

  boolean containsKey(@NotBlank String key);

  @NonNull
  <T> Optional<T> getIfPresent(@NotBlank String key, @NotNull Class<T> clazz);

  default <T> Optional<T> get(
      @NotBlank String key,
      @NotNull Class<T> clazz,
      @NotNull Supplier<? extends Optional<? extends T>> supplier) {
    var op = this.getIfPresent(key, clazz);
    if (op.isEmpty()) {
      op = op.or(supplier);
      op.ifPresent(t -> this.put(key, t));
    }
    return op;
  }

  void put(@NotBlank String key, @NotNull Object obj);

  boolean remove(@NotBlank String key);
}
