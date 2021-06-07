package com.kkk.op.support.marker;

import javax.validation.constraints.NotBlank;

/**
 * todo... 服务降级
 *
 * @author KaiKoo
 */
public interface CacheManager {

    void put(@NotBlank String key, Object obj);

    Object get(@NotBlank String key, Class<?> clazz);

    boolean remove(@NotBlank String key);

}
