package com.kkk.op.support.mock;

import com.kkk.op.support.marker.DistributedLock;
import java.util.concurrent.TimeUnit;

/**
 * todo... 通过注解或其他方式实现Mock
 * @author KaiKoo
 */
public class MockDistributedLock implements DistributedLock {

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock(String key) {
        throw new UnsupportedOperationException();
    }
}
