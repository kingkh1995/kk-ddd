package com.kkk.op.support.marker;

import java.util.Optional;
import javax.validation.constraints.NotBlank;

/**
 * todo... 服务降级、设计优化
 *
 * @author KaiKoo
 */
public interface CacheManager {

  void put(@NotBlank String key, Object obj);

  <T> Optional<T> get(@NotBlank String key, Class<T> clazz);

  boolean remove(@NotBlank String key);
}
