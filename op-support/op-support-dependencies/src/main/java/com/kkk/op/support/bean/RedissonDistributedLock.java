package com.kkk.op.support.bean;

import com.kkk.op.support.marker.DistributedLock;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * Redisson分布式锁（redlock算法）
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class RedissonDistributedLock implements DistributedLock {

    @Default
    private long expireMills = 10L * 1000L;

    private RedissonClient client;

    // 供builder使用
    private RedissonDistributedLock(long expireMills, RedissonClient client) {
        this.expireMills = expireMills;
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public boolean tryLock(@NotBlank String key, long waitTime, TimeUnit unit) {
        // 获取锁
        unit = unit == null ? TimeUnit.MILLISECONDS : unit;
        var lock = this.client.getLock(key);
        var locked = false;
        try {
            return lock.tryLock(unit.toMillis(waitTime), this.expireMills, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error(String.format("线程【%s】睡眠被中断！", Thread.currentThread().getName()), e);
        }
        return locked;
    }

    @Override
    public void unlock(@NotBlank String key) {
        this.client.getLock(key).unlock();
    }
}
