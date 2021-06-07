package com.kkk.op.support.bean;

import com.kkk.op.support.marker.CacheManager;
import java.util.Objects;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * todo...
 * @author KaiKoo
 */
public class RedisCacheManager implements CacheManager {

    private StringRedisTemplate stringRedisTemplate;

    public RedisCacheManager(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = Objects.requireNonNull(stringRedisTemplate);
    }

    @Override
    public void put(String key, Object obj) {
        // 序列化
        var v = "";
        this.stringRedisTemplate.opsForValue().set(key, v);
    }

    @Override
    public Object get(String key, Class<?> clazz) {
        var v = this.stringRedisTemplate.opsForValue().get(key);
        // 反序列化
        return null;
    }

    @Override
    public boolean remove(String key) {
        return this.stringRedisTemplate.delete(key);
    }

}
