package com.kkk.op.support.marker;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;

/**
 * 三种实现：
 *  1. setNx操作
 *  2. redlock
 *  3. zoolkeoper todo...
 *
 * @author KaiKoo
 */
public interface DistributedLock {

    /**
     * 尝试获取锁，失败不重试
     */
    default boolean tryLock(@NotBlank String key) {
        return this.tryLock(key, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * 尝试获取锁，失败则重试指定时间
     */
    boolean tryLock(@NotBlank String key, long waitTime, TimeUnit unit);

    /**
     * 释放锁
     */
    void unlock(@NotBlank String key);

    // 睡眠时间递增，并且取随机值，防止雪崩
    default long generateSleepMills(int i, long waitInterval) {
        var interval = waitInterval << i;
        return ThreadLocalRandom.current()
                .nextLong((long) (interval * 0.8), (long) (interval * 1.2));
    }

}
