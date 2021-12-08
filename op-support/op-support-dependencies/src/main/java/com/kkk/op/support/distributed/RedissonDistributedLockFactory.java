package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.NameGenerator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class RedissonDistributedLockFactory extends AbstractDistributedLockFactory {

  private final RedissonClient client;

  @Override
  public NameGenerator getLockNameGenerator() {
    return NameGenerator.joiner(":", "lock:", "");
  }

  @Override
  public DistributedLock getLock(String name) {
    return new Lock(this.client.getLock(name));
  }

  @Override
  public DistributedLock getMultiLock(List<String> names) {
    return new Lock(
        this.client.getMultiLock(names.stream().map(this.client::getLock).toArray(RLock[]::new)));
  }

  private static class Lock implements DistributedLock {

    private final RLock lock;

    public Lock(RLock lock) {
      this.lock = lock;
    }

    @Override
    public boolean tryLock() {
      return this.lock.tryLock();
    }

    @Override
    public boolean tryLock(long waitSeconds) {
      try {
        return this.lock.tryLock(waitSeconds, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        log.error(
            String.format(
                "线程【%s】获取锁【%s】被中断！", Thread.currentThread().getName(), this.lock.getName()),
            e);
        return false;
      }
    }

    @Override
    public void unlock() {
      this.lock.unlock();
    }
  }
}
