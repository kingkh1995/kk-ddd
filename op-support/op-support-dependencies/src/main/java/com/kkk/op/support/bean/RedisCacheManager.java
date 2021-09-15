package com.kkk.op.support.bean;

import com.kkk.op.support.marker.CacheManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * todo... <br>
 *
 * @author KaiKoo
 */
@RequiredArgsConstructor
public class RedisCacheManager implements CacheManager {

  private final StringRedisTemplate stringRedisTemplate;

  private final Kson kson;

  @Override
  public void put(String key, Object obj) {
    stringRedisTemplate.opsForValue().set(key, kson.writeJson(obj));
  }

  @Override
  public String get(String key) {
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
