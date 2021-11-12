package com.kkk.op.support.tool;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
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
      @NotNull Supplier<Boolean> supplier, long waitMills, long initialInterval) {
    var deadLine = System.currentTimeMillis() + waitMills;
    for (var i = 0; true; i++) {
      if (supplier.get()) {
        return true;
      } else if (System.currentTimeMillis() > deadLine) {
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
    try {
      Thread.sleep(unit.toMillis(delay));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    runnable.run();
  }
}
