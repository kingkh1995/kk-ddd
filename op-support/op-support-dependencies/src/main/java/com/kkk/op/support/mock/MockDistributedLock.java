package com.kkk.op.support.mock;

import com.kkk.op.support.marker.DistributedLock;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * todo... 通过注解或其他方式实现Mock
 *
 * @author KaiKoo
 */
@Slf4j
public class MockDistributedLock implements DistributedLock {

  @Override
  public boolean tryLock(String name, long waitTime, TimeUnit unit) {
    log.debug("Lock '{}', mock always return true!", name);
    return true;
  }

  @Override
  public void unlock(String name) {
    log.debug("Unlock '{}', mock always do nothing!", name);
  }
}
