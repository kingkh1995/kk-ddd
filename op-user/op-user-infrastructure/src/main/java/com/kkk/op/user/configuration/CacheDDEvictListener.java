package com.kkk.op.user.configuration;

import com.kkk.op.support.bean.NettyDelayer;
import com.kkk.op.support.cache.CacheDDEvictListenerSupport;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 使用时间轮算法实现延时删除缓存。<br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheDDEvictListener extends CacheDDEvictListenerSupport {

  private final NettyDelayer nettyDelayer;

  @Override
  protected void delay(Runnable runnable, long timestamp) {
    // 延迟一秒执行，从事件发生时间开始。
    nettyDelayer.delay(
        runnable, System.currentTimeMillis() - timestamp + 1000L, TimeUnit.MILLISECONDS);
  }
}
