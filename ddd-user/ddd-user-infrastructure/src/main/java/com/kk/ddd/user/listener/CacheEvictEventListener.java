package com.kk.ddd.user.listener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.kk.ddd.support.base.Kson;
import com.kk.ddd.support.bean.NettyDelayer;
import com.kk.ddd.support.cache.CacheEvictEvent;
import com.kk.ddd.support.cache.EnhancedProxyCachingConfiguration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 使用时间轮算法实现延时删除缓存。<br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
public class CacheEvictEventListener {

  private final NettyDelayer nettyDelayer;

  public CacheEvictEventListener(
      @Qualifier(EnhancedProxyCachingConfiguration.EVENT_BUS_BEAN_NAME) EventBus eventBus,
      NettyDelayer nettyDelayer) {
    eventBus.register(this);
    this.nettyDelayer = nettyDelayer;
  }

  @Subscribe
  public void onCacheEvictEvent(CacheEvictEvent event) {
    log.info("receive event: {}.", Kson.writeJson(event));
    // 延迟一秒执行，从事件发生时间开始。
    var delayNanos = 1_000_000_000L + event.getPostTime() - System.nanoTime();
    if (event.getKey() == null) {
      nettyDelayer.delay(() -> event.getCache().clear(), delayNanos, TimeUnit.NANOSECONDS);
    } else {
      nettyDelayer.delay(
          () -> event.getCache().evict(event.getKey()), delayNanos, TimeUnit.NANOSECONDS);
    }
  }
}
