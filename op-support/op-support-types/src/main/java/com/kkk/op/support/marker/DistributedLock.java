package com.kkk.op.support.marker;

import javax.validation.constraints.PositiveOrZero;

/**
 * 分布式锁对象 <br>
 *
 * @author KaiKoo
 */
public interface DistributedLock {

  boolean isLocked();

  /** 尝试获取锁，失败立即返回 */
  default boolean tryLock() {
    return this.tryLock(0L);
  }

  /** 尝试获取锁，失败阻塞则阻塞指定时间（单位秒） */
  boolean tryLock(@PositiveOrZero long waitSeconds);

  /** 释放锁 */
  void unlock();
}
