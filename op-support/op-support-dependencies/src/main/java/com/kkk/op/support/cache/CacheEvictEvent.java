package com.kkk.op.support.cache;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

/**
 * 缓存淘汰事件，用于触发延时双删。 <br>
 *
 * @author KaiKoo
 */
@Getter
public class CacheEvictEvent extends ApplicationEvent {

  @NotNull private final Cache cache;

  @Nullable private final Object key;

  public CacheEvictEvent(Object source, @NotNull Cache cache, @Nullable Object key) {
    super(source);
    this.cache = cache;
    this.key = key;
  }
}
