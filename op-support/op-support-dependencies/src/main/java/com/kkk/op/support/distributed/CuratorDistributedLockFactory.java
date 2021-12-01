package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.DistributedLockFactory;
import com.kkk.op.support.marker.NameGenerator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class CuratorDistributedLockFactory implements DistributedLockFactory {

  private final CuratorFramework client;

  @Override
  public NameGenerator getLockNameGenerator() {
    return NameGenerator.joiner("/", "/lock/", "");
  }

  @Override
  public DistributedLock getLock(String name) {
    return new Lock(new InterProcessMutex(client, name));
  }

  @Override
  public DistributedLock getMultiLock(List<String> names) {
    return new Lock(new InterProcessMultiLock(client, names));
  }

  private static class Lock implements DistributedLock {

    private final InterProcessLock lock;

    public Lock(InterProcessLock lock) {
      this.lock = lock;
    }

    @Override
    public boolean isLocked() {
      return this.lock.isAcquiredInThisProcess();
    }

    @Override
    public boolean tryLock(long waitSeconds) {
      try {
        return this.lock.acquire(waitSeconds, TimeUnit.SECONDS);
      } catch (Exception e) {
        log.error("CuratorDistributedLocke lock errro!", e);
        return false;
      }
    }

    @Override
    public void unlock() {
      try {
        this.lock.release();
      } catch (Exception e) {
        log.error("CuratorDistributedLocke unlock errro!", e);
      }
    }
  }
}
