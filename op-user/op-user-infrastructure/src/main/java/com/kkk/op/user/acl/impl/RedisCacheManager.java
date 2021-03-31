package com.kkk.op.user.acl.impl;

import com.kkk.op.support.marker.CacheManager;
import org.springframework.stereotype.Component;

/**
 * todo... 实现 & 服务降级 设置为公共bean
 * @author KaiKoo
 */
@Component
public class RedisCacheManager<T> implements CacheManager<T> {

    @Override
    public boolean cachePut(String key, T t) {
        return false;
    }

    @Override
    public T cacheGet(String key) {
        return null;
    }

    @Override
    public boolean cacheRemove(String key) {
        return false;
    }
}
