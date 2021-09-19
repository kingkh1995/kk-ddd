package com.kkk.op.support.cache;

import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.marker.CacheManager;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * todo... 参考RedisCacheWriter设计 <br>
 *
 * @author KaiKoo
 */
@Builder
public class RedisCacheManager implements CacheManager {

  private final StringRedisTemplate stringRedisTemplate;

  private final long expireAtferMills;

  private final Kson kson;

  // for builder to call
  private RedisCacheManager(
      StringRedisTemplate stringRedisTemplate, long expireAtferMills, Kson kson) {
    this.stringRedisTemplate = Objects.requireNonNull(stringRedisTemplate);
    if (expireAtferMills <= 0) {
      throw new IllegalArgumentException("Expire after mills should be greater than 0!");
    }
    this.expireAtferMills = expireAtferMills;
    this.kson = Objects.requireNonNull(kson);
  }

  @Override
  public boolean containsKey(String key) {
    return stringRedisTemplate.hasKey(key);
  }

  private String get(@NotBlank String key) {
    return stringRedisTemplate.opsForValue().get(key);
  }

  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    return Optional.ofNullable(get(key)).map(content -> kson.readJson(content, clazz));
  }

  @Override
  public void put(String key, Object obj) {
    stringRedisTemplate
        .opsForValue()
        .set(key, kson.writeJson(obj), expireAtferMills, TimeUnit.MILLISECONDS);
  }

  @Override
  public boolean remove(String key) {
    return stringRedisTemplate.delete(key);
  }
}
