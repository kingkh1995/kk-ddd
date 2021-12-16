package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.DistributedLockFactory;
import com.kkk.op.support.marker.NameGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * zookeeper分布式锁，基于临时顺序节点 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class CuratorDistributedLockFactory implements DistributedLockFactory {

  @NonNull private final CuratorFramework client;

  // ThreadLocal使用时尽量用static修饰、理论上不会出现内存泄漏，因为加锁成功后就一定会释放锁。
  private static final ThreadLocal<Map<String, InterProcessMutex>> holder =
      ThreadLocal.withInitial(HashMap::new);

  private InterProcessMutex getMutex(String name) {
    return holder
        .get()
        .compute(
            name, (k, v) -> Optional.ofNullable(v).orElse(new InterProcessMutex(client, name)));
  }

  private void cleanIfNeed(String name) {
    if (null
        == holder.get().computeIfPresent(name, (k, v) -> v.isOwnedByCurrentThread() ? v : null)) {
      log.info("clean '{}', now size '{}'.", name, holder.get().size());
    }
  }

  @Override
  public NameGenerator getLockNameGenerator() {
    return NameGenerator.joiner("/", "/lock/", "");
  }

  @Override
  public DistributedLock getLock(String name) {
    return new Lock(this, name);
  }

  @Override
  public DistributedLock getMultiLock(List<String> names) {
    return new MultiLock(
        this,
        names,
        new InterProcessMultiLock(names.stream().map(this::getMutex).collect(Collectors.toList())));
  }

  @AllArgsConstructor
  private static class Lock implements DistributedLock {

    private final CuratorDistributedLockFactory factory;

    private final String name;

    private InterProcessMutex getMutex() {
      return factory.getMutex(name);
    }

    @Override
    public boolean tryLock(long waitSeconds) {
      try {
        return getMutex().acquire(waitSeconds, TimeUnit.SECONDS);
      } catch (Exception e) {
        log.error("CuratorDistributedLock lock errro!", e);
        return false;
      } finally {
        cleanIfNeed();
      }
    }

    @Override
    public void unlock() {
      try {
        getMutex().release();
      } catch (Exception e) {
        log.error("CuratorDistributedLock unlock errro!", e);
      } finally {
        cleanIfNeed();
      }
    }

    private void cleanIfNeed() {
      factory.cleanIfNeed(name);
    }
  }

  @AllArgsConstructor
  private static class MultiLock implements DistributedLock {

    private final CuratorDistributedLockFactory factory;

    private final List<String> names;

    private final InterProcessMultiLock multiLock;

    @Override
    public boolean tryLock(long waitSeconds) {
      try {
        return this.multiLock.acquire(waitSeconds, TimeUnit.SECONDS);
      } catch (Exception e) {
        log.error("CuratorDistributedLock lock errro!", e);
        return false;
      } finally {
        cleanIfNeed();
      }
    }

    @Override
    public void unlock() {
      try {
        this.multiLock.release();
      } catch (Exception e) {
        log.error("CuratorDistributedLock unlock errro!", e);
      } finally {
        cleanIfNeed();
      }
    }

    private void cleanIfNeed() {
      this.names.forEach(this.factory::cleanIfNeed);
    }
  }
}
