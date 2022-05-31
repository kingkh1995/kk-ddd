package com.kk.ddd.support.bean;

import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Netty单层时间轮算法 <br>
 * 适用于时效性不高，可快速执行，数量巨大的延迟任务，如心跳检测等。
 *
 * @author KaiKoo
 */
public class NettyDelayer {

  /** HashedWheelTimer默认执行线程池为ImmediateExecutor（直接由workerThread执行） */
  private final HashedWheelTimer timer;

  public NettyDelayer() {
    this.timer =
        new HashedWheelTimer(
            new DefaultThreadFactory("NettyDelayerPool"),
            100,
            TimeUnit.MILLISECONDS,
            512, // 会调整为2的幂
            false,
            -1,
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() << 1));
  }

  public NettyDelayer(final HashedWheelTimer timer) {
    this.timer = Objects.requireNonNull(timer);
  }

  public void delay(Runnable runnable, long delay, TimeUnit unit) {
    timer.newTimeout(timeout -> runnable.run(), delay, unit);
  }
}
