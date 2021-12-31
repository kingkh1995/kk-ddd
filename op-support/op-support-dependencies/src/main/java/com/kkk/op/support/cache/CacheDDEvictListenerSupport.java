package com.kkk.op.support.cache;

import org.springframework.context.ApplicationListener;

/**
 * 缓存延时双删监听器 <br>
 *
 * @author KaiKoo
 */
public abstract class CacheDDEvictListenerSupport implements ApplicationListener<CacheEvictEvent> {

  protected abstract void delay(Runnable runnable, long timestamp);

  @Override
  public void onApplicationEvent(CacheEvictEvent event) {
    if (event.getKey() == null) {
      delay(() -> event.getCache().clear(), event.getTimestamp());
    } else {
      delay(() -> event.getCache().evict(event.getKey()), event.getTimestamp());
    }
  }
}
