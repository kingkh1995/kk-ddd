package com.kkk.op.support.bean;

import com.kkk.op.support.marker.DistributedLock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * 基于redis的分布式锁实现 （适用于redis单机模式，或者对可用性要求不是特别高）
 * 集群模式redis下有一个明显的竞争条件，因为复制是异步的，客户端A在master节点拿到了锁，master节点在把A创建的key写入slave之前宕机了
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class RedisDistributedLock implements DistributedLock {

    @Default
    private long sleepInterval = 1L << 8;

    @Default
    private long expireMills = 10L * 1000L;

    private StringRedisTemplate redisTemplate;

    private final ThreadLocal<Map<String, LockInfo>> lockContext = ThreadLocal
            .withInitial(HashMap::new);

    // 供builder使用
    private RedisDistributedLock(long sleepInterval, long expireMills,
            StringRedisTemplate redisTemplate) {
        this.sleepInterval = sleepInterval;
        this.expireMills = expireMills;
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
    }

    private static class LockInfo {

        private String key;

        private String requestId = UUID.randomUUID().toString();

        private int count = 1;

        public LockInfo(String key) {
            this.key = key;
        }

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
        lockInfo = new LockInfo(key);
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
            // 开启watchdog
            watching(lockInfo);
        }
        return locked;
    }

    private final static RedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('DEL', KEYS[1]) else return 0 end",
            Long.class);

    /**
     * 使用lua脚本进行原子释放锁
     * <p>
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
                    .execute(UNLOCK_SCRIPT, Collections.singletonList(key), lockInfo.requestId);
            if (result < 1) {
                log.warn(String.format("execute return %d!", result));
            }
        } catch (Exception e) {
            log.warn("unlock error!", e);
        } finally {
            this.lockContext.get().remove(key);
        }
    }

    private final ScheduledExecutorService WATCH_DOG = Executors
            .newSingleThreadScheduledExecutor(r -> {
                // 设置为守护线程
                var t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            });

    private final static RedisScript<Long> WATCH_DOG_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('SET', KEYS[1], ARGV[1], 'XX', 'PX', ARGV[2]) else return 0 end",
            Long.class);

    private void watching(LockInfo lockInfo) {
        WATCH_DOG.schedule(() -> {
            try {
                var result = this.redisTemplate
                        .execute(WATCH_DOG_SCRIPT, Collections.singletonList(lockInfo.key),
                                lockInfo.requestId, expireMills);
                // 如果延长成功，继续watch
                if (result > 0) {
                    watching(lockInfo);
                }
            } catch (Exception e) {
                log.warn("watch dog error!", e);
            }
        }, expireMills * 9 / 10, TimeUnit.MILLISECONDS);
    }

}
