package com.kk.ddd.support.cache;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

/**
 * 缓存淘汰事件，用于触发延时双删。 <br>
 *
 * @author KaiKoo
 */
@Getter
public class CacheEvictEvent {

  @NotNull private final Cache cache;

  @Nullable private final Object key;

  // 使用System.nanoTime()计时，会返回从某个任意时刻（包括未来）开始的纳秒值，故值可以为任意，通过比较后进行计时。
  private final long postTime = System.nanoTime();

  public CacheEvictEvent(@NotNull Cache cache, @Nullable Object key) {
    this.cache = cache;
    this.key = key;
  }
}
