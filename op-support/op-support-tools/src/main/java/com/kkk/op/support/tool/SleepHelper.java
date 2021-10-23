package com.kkk.op.support.tool;

import java.util.concurrent.ThreadLocalRandom;
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
  public static long generateSleepMills(int i, long initialInterval) {
    var interval = initialInterval << i;
    return ThreadLocalRandom.current().nextLong((long) (interval * 0.8), (long) (interval * 1.2));
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
          Thread.sleep(generateSleepMills(i, initialInterval));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
