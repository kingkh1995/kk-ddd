package com.kkk.op.support.bean;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Netty时间轮算法 <br>
 *
 * @author KaiKoo
 */
public class NettyDelayer {

  /** HashedWheelTimer默认线程池为ImmediateExecutor（直接执行） */
  private final Timer timer;

  public NettyDelayer() {
    this.timer =
        new HashedWheelTimer(
            new DefaultThreadFactory("NettyDelayerPool"),
            100,
            TimeUnit.MILLISECONDS,
            512,
            false,
            0,
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1));
  }

  public NettyDelayer(final Timer timer) {
    this.timer = Objects.requireNonNull(timer);
  }

  public void delay(Runnable runnable, long delay, TimeUnit unit) {
    timer.newTimeout(timeout -> runnable.run(), delay, unit);
  }
}
