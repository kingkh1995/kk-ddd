package com.kkk.op.support.bean;

import com.kkk.op.support.marker.Cache;
import java.util.Objects;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * todo... 实现 & 服务降级
 * @author KaiKoo
 */
public class RedisCache<T> implements Cache<T> {

    private StringRedisTemplate stringRedisTemplate;

    public RedisCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = Objects.requireNonNull(stringRedisTemplate);
    }

    @Override
    public boolean put(String key, T t) {
        return false;
    }

    @Override
    public T get(String key) {
        return null;
    }

    @Override
    public boolean remove(String key) {
        return false;
    }
}
