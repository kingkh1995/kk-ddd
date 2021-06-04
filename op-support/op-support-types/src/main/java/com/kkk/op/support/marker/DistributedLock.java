package com.kkk.op.support.marker;

import com.kkk.op.support.function.Worker;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;

/**
 * 三种实现：
 *  1. setNx操作
 *  2. redlock（redisson实现）
 *  3. zookeeper // todo...
 *
 * @author KaiKoo
 */
public interface DistributedLock {

    /**
     * 获取锁并执行一段工作，获取锁失败立即返回，执行完成自动释放锁。
     */
    default boolean tryWork(@NotBlank String key, Worker worker) {
        return this.tryWork(key, 0L, TimeUnit.MILLISECONDS, worker);
    }

    /**
     * 获取锁并执行一段工作，获取锁失败则阻塞指定时间，执行完成自动释放锁。
     */
    default boolean tryWork(@NotBlank String key, long waitTime, TimeUnit unit, Worker worker) {
        if (this.tryLock(key, waitTime, unit)) {
            try {
                worker.work();
                return true;
            } finally {
                this.unlock(key);
            }
        }
        return false;
    }

    /**
     * 尝试获取锁，失败立即返回
     */
    default boolean tryLock(@NotBlank String key) {
        return this.tryLock(key, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * 尝试获取锁，失败阻塞则阻塞指定时间
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
