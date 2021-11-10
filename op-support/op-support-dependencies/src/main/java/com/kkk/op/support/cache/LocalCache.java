package com.kkk.op.support.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.util.Assert;

/**
 * local cache using caffeine <br>
 *
 * @author KaiKoo
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalCache extends SpringCacheWrapper {

  @Getter private final CaffeineCache cache;

  public static LocalCacheBuilder builder() {
    return new LocalCacheBuilder();
  }

  @Setter
  @Accessors(fluent = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class LocalCacheBuilder {
    private String name;
    private Cache<Object, Object> caffeineCache;

    public LocalCache build() {
      Assert.hasText(this.name, "Is empty!");
      Assert.notNull(this.caffeineCache, "Is null!");
      // always allow null values
      var caffeineCache = new CaffeineCache(this.name, this.caffeineCache, true);
      return new LocalCache(caffeineCache);
    }
  }
}
