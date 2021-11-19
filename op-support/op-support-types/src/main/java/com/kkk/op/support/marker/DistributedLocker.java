package com.kkk.op.support.marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;

/**
 * 三种实现： <br>
 * 1. setNx操作 2. redlock（redisson实现） 3. zookeeper //todo...
 *
 * @author KaiKoo
 */
@Validated
public interface DistributedLocker {

  default NameGenerator getLockNameGenerator() {
    return NameGenerator.joiner("#", "", "");
  }

  /** 获取锁并执行一段任务，获取锁失败立即返回，执行完成自动释放锁。 */
  default boolean tryRun(@NotBlank String name, @NotNull Runnable runnable) {
    return this.tryRun(name, 0L, runnable);
  }

  /** 获取锁并执行一段任务，获取锁失败则阻塞指定时间（单位秒），执行完成自动释放锁。 */
  default boolean tryRun(
      @NotBlank String name, @PositiveOrZero long waitSeconds, @NotNull Runnable runnable) {
    if (this.tryLock(name, waitSeconds)) {
      try {
        runnable.run();
        return true;
      } finally {
        this.unlock(name);
      }
    }
    return false;
  }

  /** 尝试获取锁，失败立即返回 */
  default boolean tryLock(@NotBlank String name) {
    return this.tryLock(name, 0L);
  }

  /** 尝试获取锁，失败阻塞则阻塞指定时间（单位秒） */
  boolean tryLock(@NotBlank String name, @PositiveOrZero long waitSeconds);

  /** 释放锁 */
  void unlock(@NotBlank String name);
}
