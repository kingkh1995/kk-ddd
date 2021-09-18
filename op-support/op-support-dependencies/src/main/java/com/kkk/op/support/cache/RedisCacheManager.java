package com.kkk.op.support.cache;

import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.marker.CacheManager;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * todo... 参考RedisCacheManager设计 <br>
 *
 * @author KaiKoo
 */
@Builder
public class RedisCacheManager implements CacheManager {

  private final StringRedisTemplate stringRedisTemplate;

  private final Kson kson;

  private RedisCacheManager(StringRedisTemplate stringRedisTemplate, Kson kson) {
    this.stringRedisTemplate = Objects.requireNonNull(stringRedisTemplate);
    this.kson = Objects.requireNonNull(kson);
  }

  @Override
  public void put(String key, Object obj) {
    stringRedisTemplate.opsForValue().set(key, kson.writeJson(obj));
  }

  private String get(String key) {
    return stringRedisTemplate.opsForValue().get(key);
  }

  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    return Optional.ofNullable(get(key)).map(content -> kson.readJson(content, clazz));
  }

  @Override
  public boolean remove(String key) {
    return stringRedisTemplate.delete(key);
  }
}
