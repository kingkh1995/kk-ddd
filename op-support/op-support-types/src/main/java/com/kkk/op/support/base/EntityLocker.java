package com.kkk.op.support.base;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.DistributedLockFactory;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * 分布式锁工具类 <br>
 *
 * @author KaiKoo
 */
public final class EntityLocker {

  private static DistributedLockFactory FACTORY;

  public static void setFactory(@NotNull DistributedLockFactory factory) {
    FACTORY = factory;
  }

  private EntityLocker() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  public static DistributedLock getLock(@NotNull Entity<?> entity) {
    return FACTORY.getLock(entity.generateLockName(FACTORY.getLockNameGenerator()));
  }

  public static DistributedLock getMultiLock(@Size(min = 2) List<Entity<?>> entities) {
    return FACTORY.getMultiLock(
        entities.stream()
            .map(entity -> entity.generateLockName(FACTORY.getLockNameGenerator()))
            .toList());
  }

  public static boolean tryLockThenRun(@NotNull Entity<?> entity, @NotNull Runnable runnable) {
    return tryLockThenRun(entity, 0L, runnable);
  }

  public static boolean tryLockThenRun(
      @NotNull Entity<?> entity, @PositiveOrZero long waitSeconds, @NotNull Runnable runnable) {
    return tryLockThenRun(getLock(entity), waitSeconds, runnable);
  }

  public static boolean tryLockThenRun(
      @Size(min = 2) List<Entity<?>> entities, @NotNull Runnable runnable) {
    return tryLockThenRun(entities, 0L, runnable);
  }

  public static boolean tryLockThenRun(
      @Size(min = 2) List<Entity<?>> entities,
      @PositiveOrZero long waitSeconds,
      @NotNull Runnable runnable) {
    return tryLockThenRun(getMultiLock(entities), waitSeconds, runnable);
  }

  /** 获取锁并执行一段任务，获取锁失败则阻塞指定时间（单位秒），执行完成自动释放锁。 */
  private static boolean tryLockThenRun(
      @NotNull DistributedLock lock, @PositiveOrZero long waitSeconds, @NotNull Runnable runnable) {
    if (lock.tryLock(waitSeconds)) {
      try {
        runnable.run();
        return true;
      } finally {
        lock.unlock();
      }
    }
    return false;
  }
}
