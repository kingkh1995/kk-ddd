package com.kkk.op.support.bean;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 时间轮工具类 <br>
 *
 * @author KaiKoo
 */
public class WheelTimer {

  /** netty时间轮，默认512个tick，tickDuration越小精度越高 */
  private final HashedWheelTimer timer;

  /** 额外线程池，HashedWheelTimer只有一个线程用于处理任务，故任务到期后，将任务提交给线程池处理，提高处理能力。 */
  private final Executor executor;

  public WheelTimer(long tickDuration, TimeUnit tickDurationUnit) {
    this.timer = new HashedWheelTimer(tickDuration, tickDurationUnit);
    // todo... 默认线程池暂定
    this.executor =
        Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new DefaultThreadFactory("CommonWheelTimerPool"));
  }

  public WheelTimer(long tickDuration, TimeUnit tickDurationUnit, Executor executor) {
    this.timer = new HashedWheelTimer(tickDuration, tickDurationUnit);
    this.executor = executor;
  }

  public Timeout delay(Runnable runnable, long delay, TimeUnit unit) {
    return timer.newTimeout(timeout -> executor.execute(runnable), delay, unit);
  }
}
