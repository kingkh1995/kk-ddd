package com.kkk.op.support.cache;

import org.springframework.beans.BeansException;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 缓存拦截器增强 <br>
 *
 * @author KaiKoo
 */
public class EnhancedCacheInterceptor extends CacheInterceptor implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  protected void doEvict(Cache cache, Object key, boolean immediate) {
    super.doEvict(cache, key, immediate);
    applicationContext.publishEvent(new CacheEvictEvent(this, cache, key));
  }

  @Override
  protected void doClear(Cache cache, boolean immediate) {
    super.doClear(cache, immediate);
    applicationContext.publishEvent(new CacheEvictEvent(this, cache, null));
  }
}
