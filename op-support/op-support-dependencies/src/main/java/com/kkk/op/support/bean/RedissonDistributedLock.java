package com.kkk.op.support.bean;

import com.kkk.op.support.marker.DistributedLock;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * Redisson分布式锁（redlock算法）
 *
 * @author KaiKoo
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedissonDistributedLock implements DistributedLock {

    private long expireMills;

    private RedissonClient client;

    public static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public final static class Builder {

        private long expireMills = 10L * 1000L;

        private RedissonClient client;

        public Builder expireMills(long expireMills) {
            this.expireMills = expireMills;
            return this;
        }

        public Builder RedissonClient(RedissonClient client) {
            this.client = client;
            return this;
        }

        public RedissonDistributedLock build() {
            var redissonDistributedLock = new RedissonDistributedLock();
            redissonDistributedLock.expireMills = this.expireMills;
            redissonDistributedLock.client = Objects.requireNonNull(this.client);
            return redissonDistributedLock;
        }
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
