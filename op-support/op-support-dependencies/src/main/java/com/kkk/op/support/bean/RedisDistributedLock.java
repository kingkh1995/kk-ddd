package com.kkk.op.support.bean;

import com.kkk.op.support.marker.DistributedLock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * 基于redis的分布式锁实现 （适用于redis单机模式，或者对可用性要求不是特别高）
 * 集群模式redis下有一个明显的竞争条件，因为复制是异步的，客户端A在master节点拿到了锁，master节点在把A创建的key写入slave之前宕机了
 *
 * todo... watchdog:自动延长锁时间
 *
 * @author KaiKoo
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisDistributedLock implements DistributedLock {

    private long sleepInterval;

    private long expireMills;

    private StringRedisTemplate redisTemplate;

    private final ThreadLocal<Map<String, LockInfo>> lockContext = ThreadLocal
            .withInitial(HashMap::new);

    public static Builder builder() {
        return new Builder();
    }

    // Builder模式
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public final static class Builder {

        private long sleepInterval = 200L;

        private long expireMills = 10L * 1000L;

        private StringRedisTemplate redisTemplate;

        public Builder sleepInterval(long sleepInterval) {
            this.sleepInterval = sleepInterval;
            return this;
        }

        public Builder expireMills(long expireMills) {
            this.expireMills = expireMills;
            return this;
        }

        public Builder redisTemplate(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
            return this;
        }

        public RedisDistributedLock build() {
            var redisDistributedLock = new RedisDistributedLock();
            redisDistributedLock.sleepInterval = this.sleepInterval;
            redisDistributedLock.expireMills = this.expireMills;
            redisDistributedLock.redisTemplate = Objects.requireNonNull(this.redisTemplate);
            return redisDistributedLock;
        }
    }

    private static class LockInfo {

        private int count = 1;

        private String requestId = UUID.randomUUID().toString();

    }

    private LockInfo getLockInfo(String key) {
        return this.lockContext.get().get(key);
    }

    private boolean lock(String key, String requestId) {
        return this.redisTemplate.opsForValue()
                .setIfAbsent(key, requestId, expireMills, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取锁，失败则自旋重试
     */
    @Override
    public boolean tryLock(@NotBlank String key, long waitTime, TimeUnit unit) {
        // 可重入锁，判断该线程是否获取到了锁
        var lockInfo = this.getLockInfo(key);
        if (lockInfo != null) {
            lockInfo.count++;
            return true;
        }
        // 获取锁
        unit = unit == null ? TimeUnit.MILLISECONDS : unit;
        var waitMills = unit.toMillis(waitTime);
        lockInfo = new LockInfo();
        var locked = this.lock(key, lockInfo.requestId);
        for (var i = 0; !locked && waitMills > 0; i++) {
            try {
                var interval = generateSleepMills(i, this.sleepInterval);
                Thread.sleep(interval);
                waitMills -= interval;
            } catch (InterruptedException e) {
                log.error(String.format("线程【%s】睡眠被中断！", Thread.currentThread().getName()), e);
            }
            locked = this.lock(key, lockInfo.requestId);
        }
        if (locked) {
            // 获取到锁保存锁信息
            this.lockContext.get().put(key, lockInfo);
        }
        return locked;
    }

    private final static String SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 使用lua脚本进行原子释放锁
     *
     * 解决问题：如果锁已自动释放请求仍未执行完，则可能会让被其他线程获取到该锁，必须保证不能释放掉别人加的锁
     * （同时业务逻辑中也要通过乐观锁或其他方式避免并发问题发生）
     */
    public void unlock(@NotBlank String key) {
        var lockInfo = this.getLockInfo(key);
        if (lockInfo == null) {
            return;
        }
        if (--lockInfo.count > 0) {
            return;
        }
        // 释放锁
        try {
            var result = this.redisTemplate
                    .execute(new DefaultRedisScript<>(SCRIPT, Long.class), Arrays.asList(key),
                            lockInfo.requestId);
            if (result < 1) {
                log.warn(String.format("execute return %d!", result));
            }
        } catch (Exception e) {
            log.warn("unlock error!", e);
        } finally {
            this.lockContext.get().remove(key);
        }
    }

}
