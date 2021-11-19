package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLocker;
import com.kkk.op.support.marker.NameGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * zookeeper分布式锁，基于临时顺序节点 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class CuratorDistributedLocker implements DistributedLocker {

  private final CuratorFramework client;

  private static final ThreadLocal<Map<String, InterProcessMutex>> holder =
      ThreadLocal.withInitial(HashMap::new);

  @Override
  public NameGenerator getLockNameGenerator() {
    return NameGenerator.joiner("/", "/lock/", "");
  }

  @Override
  public boolean tryLock(String name, long waitSeconds) {
    var lock = holder.get().getOrDefault(name, new InterProcessMutex(client, name));
    var isAcquired = false;
    try {
      isAcquired = lock.acquire(waitSeconds, TimeUnit.SECONDS);
    } catch (Exception e) {
      log.error("CuratorDistributedLocker lock errro!", e);
    }
    if (isAcquired) {
      holder.get().putIfAbsent(name, lock);
    }
    return isAcquired;
  }

  @Override
  public void unlock(String name) {
    var lock = holder.get().get(name);
    if (lock == null) {
      log.warn("CuratorDistributedLocker unlock '{}' not exists!", name);
    } else {
      try {
        lock.release();
      } catch (Exception e) {
        log.error("CuratorDistributedLocker unlock errro!", e);
      }
      if (!lock.isOwnedByCurrentThread()) {
        holder.get().remove(name);
      }
    }
  }
}
