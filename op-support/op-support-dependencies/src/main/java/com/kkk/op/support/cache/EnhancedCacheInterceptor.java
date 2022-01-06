package com.kkk.op.support.cache;

import com.google.common.eventbus.EventBus;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;

/**
 * 缓存拦截器增强 <br>
 *
 * @author KaiKoo
 */
@AllArgsConstructor
public class EnhancedCacheInterceptor extends CacheInterceptor {

  private final EventBus eventBus;

  @Override
  protected void doEvict(Cache cache, Object key, boolean immediate) {
    super.doEvict(cache, key, immediate);
    this.eventBus.post(new CacheEvictEvent(cache, key));
  }

  @Override
  protected void doClear(Cache cache, boolean immediate) {
    super.doClear(cache, immediate);
    this.eventBus.post(new CacheEvictEvent(cache, null));
  }
}
