package com.kkk.op.support.distributed;

import com.kkk.op.support.base.EntityLocker;
import com.kkk.op.support.marker.DistributedLockFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * DistributedLockFactory抽象类，bean创建完成后自动设置到EntityLocker中。 <br>
 *
 * @author KaiKoo
 */
public abstract class AbstractDistributedLockFactory
    implements DistributedLockFactory, InitializingBean {

  @Override
  public void afterPropertiesSet() {
    EntityLocker.setFactory(this);
  }
}
