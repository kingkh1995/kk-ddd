package com.kkk.op.support.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.validation.constraints.NotNull;

/**
 * 线程睡眠循环执行复制器 <br>
 *
 * @author KaiKoo
 */
public final class SleepHelper {

  public SleepHelper() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  // 睡眠时间递增，并且取随机值，防止雪崩
  public static long generateSleepMills(int i, long initialInterval, long maxInterval) {
    var interval = initialInterval << i;
    interval = interval > 0 && interval < maxInterval ? interval : maxInterval;
    // todo... 随机数生成
    return interval + (ThreadLocalRandom.current().nextLong(-interval, interval) >> 3);
  }

  public static boolean tryGetThenSleep(
      final @NotNull BooleanSupplier supplier, final long waitMills, final long initialInterval) {
    var deadLine = System.nanoTime() + waitMills * 1_000_000L;
    for (var i = 0; true; i++) {
      if (supplier.getAsBoolean()) {
        return true;
      } else if (System.nanoTime() > deadLine) {
        return false;
      } else {
        try {
          Thread.sleep(generateSleepMills(i, initialInterval, 2048L));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void delay(@NotNull Runnable runnable, long delay, TimeUnit unit) {
    // not recommended
    CompletableFuture.runAsync(
            () -> {
              try {
                Thread.sleep(unit.toMillis(delay));
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              runnable.run();
            })
        .whenComplete((unused, throwable) -> {});
  }
}
