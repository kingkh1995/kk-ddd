package com.kkk.op.support.bean;

import com.kkk.op.support.marker.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * todo... 实现 & 服务降级
 * @author KaiKoo
 */
//@Component // fixme... 暂时未开放redis功能
public class RedisCacheManager<T> implements CacheManager<T> {

    private StringRedisTemplate stringRedisTemplate;

    public RedisCacheManager(@Autowired StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

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
