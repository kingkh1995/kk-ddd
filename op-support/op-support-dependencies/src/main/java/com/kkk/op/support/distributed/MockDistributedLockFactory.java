package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.DistributedLockFactory;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * MockDistributedLockeFactory
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class MockDistributedLockFactory implements DistributedLockFactory {

  @Override
  public DistributedLock getLock(String name) {
    return new MockLock(name);
  }

  @Override
  public DistributedLock getMultiLock(List<String> names) {
    return new MockLock(names.toArray(new String[0]));
  }

  private static final class MockLock implements DistributedLock {

    private final String[] names;

    public MockLock(String... names) {
      this.names = names;
    }

    @Override
    public boolean tryLock(long waitSeconds) {
      log.info("Lock '{}', mock always return true!", Arrays.toString(names));
      return true;
    }

    @Override
    public void unlock() {
      log.info("Unlock '{}', mock always do nothing!", Arrays.toString(names));
    }
  }
}
