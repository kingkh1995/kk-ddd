package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * Redisson分布式锁（redlock算法）
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class RedissonDistributedLock implements DistributedLock {

  @Default private long expireMills = 10L * 1000L;

  private final RedissonClient client;

  // 供builder使用
  public RedissonDistributedLock(long expireMills, RedissonClient client) {
    if (expireMills <= 0) {
      throw new IllegalArgumentException(
          "RedissonDistributedLock expireMills should be greater than 0!");
    }
    this.expireMills = expireMills;
    this.client = Objects.requireNonNull(client);
  }

  @Override
  public boolean tryLock(String name, long waitSeconds) {
    var locked = false;
    try {
      return this.client.getLock(name).tryLock(waitSeconds, this.expireMills, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error(String.format("线程【%s】睡眠被中断！", Thread.currentThread().getName()), e);
    }
    return locked;
  }

  @Override
  public void unlock(String name) {
    this.client.getLock(name).unlock();
  }
}
