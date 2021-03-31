package com.kkk.op.support.bean;

import com.kkk.op.support.exception.BussinessException;
import com.kkk.op.support.marker.DistributedReentrantLock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * 分布式锁实现
 * todo... 可重入锁实现
 * @author KaiKoo
 */
@Slf4j
public class RedisDistributedReentrantLock implements DistributedReentrantLock {

    private final StringRedisTemplate redisTemplate;

    private final ThreadLocal<Map<String, String>> context = ThreadLocal.withInitial(HashMap::new);

    @Setter
    private int maxRetryTimes = 3;

    @Setter
    private long sleepInterval = 500L;

    @Setter
    private long lockTime = 10L;

    @Setter
    private TimeUnit lockTimeUnit = TimeUnit.SECONDS;

    public RedisDistributedReentrantLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 睡眠时间递增，并且取随机值，防止雪崩
    private long generateSleepMills(int i) {
        var interval = sleepInterval << i;
        return ThreadLocalRandom.current()
                .nextLong((long) (interval * 0.8), (long) (interval * 1.2));
    }

    // 生成 UUID 作为 RequestId 并保存在 ThreadLocal 中 释放锁的时候再取出值做对比
    private String generateRequestId(String key) {
        var uuid = UUID.randomUUID().toString();
        context.get().put(key, uuid);
        return uuid;
    }

    private String getRequestId(String key) {
        return context.get().get(key);
    }

    private boolean lock(String key, String requestId) {
        return redisTemplate.opsForValue().setIfAbsent(key, requestId, lockTime, lockTimeUnit);
    }

    /**
     * 获取锁，失败不重试
     */
    public void tryLock(@NotBlank String key) {
        this.tryLock(key, 0);
    }

    /**
     * 获取锁，失败则自旋重试
     */
    public void tryLock(@NotBlank String key, int retry) {
        var locked = false;
        var i = 0;
        retry = retry > maxRetryTimes ? maxRetryTimes : retry;
        var requestId = generateRequestId(key);
        locked = this.lock(key, requestId);
        while (!locked && retry > 0) {
            try {
                Thread.sleep(generateSleepMills(i++));
            } catch (InterruptedException e) {
                log.error(String.format("线程【%s】睡眠被中断！", Thread.currentThread().getName()), e);
            }
            locked = this.lock(key, requestId);
            retry--;
        }
        if (!locked) {
            throw new BussinessException("服务繁忙请稍后再试！");
        }
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
        var requestId = getRequestId(key);
        if (requestId == null) {
            return;
        }
        try {
            var result = redisTemplate
                    .execute(new DefaultRedisScript<>(SCRIPT, Long.class), Arrays.asList(key),
                            Arrays.asList(requestId));
            if (result < 1) {
                log.warn(String.format("execute return %d!", result));
            }
        } catch (Exception e) {
            log.warn("unlock error!", e);
        }
    }
}
