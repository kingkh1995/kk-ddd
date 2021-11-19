package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLocker;
import com.kkk.op.support.marker.NameGenerator;
import com.kkk.op.support.tool.SleepHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * 基于redis的分布式锁实现 （适用于redis单机模式，或者对可用性要求不是特别高） <br>
 * 主从模式redis下有一个明显的竞争条件，因为复制是异步的，客户端A在master节点拿到了锁，master节点在把A创建的key写入slave之前宕机了
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class RedisDistributedLocker implements DistributedLocker {

  @Default private long sleepInterval = 200;

  @Default private long expireMills = 10L * 1000L;

  private final StringRedisTemplate redisTemplate;

  // ThreadLocal使用时尽量用static修饰、理论上不会出现内存泄漏，因为加锁成功后就一定会释放锁。
  private static final ThreadLocal<Map<String, Lock>> holder =
      ThreadLocal.withInitial(HashMap::new);

  // 供builder使用
  public RedisDistributedLocker(
      long sleepInterval, long expireMills, StringRedisTemplate redisTemplate) {
    if (expireMills <= 0) {
      throw new IllegalArgumentException(
          "RedisDistributedLock expireMills should be greater than 0!");
    }
    this.sleepInterval = sleepInterval;
    this.expireMills = expireMills;
    this.redisTemplate = Objects.requireNonNull(redisTemplate);
  }

  @Override
  public NameGenerator getLockNameGenerator() {
    return NameGenerator.joiner(":", "lock:", "");
  }

  private static class Lock {

    private final String requestId = UUID.randomUUID().toString();

    private int status = 1;

    private Future<?> future;
  }

  private Optional<Lock> getLock(String name) {
    return Optional.ofNullable(holder.get().get(name));
  }

  private boolean lock(String name, String requestId) {
    var result =
        this.redisTemplate
            .opsForValue()
            .setIfAbsent(name, requestId, this.expireMills, TimeUnit.MILLISECONDS);
    log.info("Try lock '{}' using '{}', return '{}'.", name, requestId, result);
    return result != null && result;
  }

  /** 获取锁，失败则自旋重试 */
  @Override
  public boolean tryLock(String name, long waitSeconds) {
    // 可重入锁，判断该线程是否获取到了锁
    var op = this.getLock(name);
    // 已获取到锁则次数加一
    if (op.isPresent()) {
      op.get().status++;
      return true;
    }
    // 尝试获取锁
    var lock = new Lock();
    var isLocked =
        SleepHelper.tryGetThenSleep(
            () -> this.lock(name, lock.requestId),
            TimeUnit.SECONDS.toMillis(waitSeconds),
            this.sleepInterval);
    if (isLocked) {
      // 获取到锁 则保存锁信息
      holder.get().put(name, lock);
      // 开启watchdog
      watching(name, lock);
    }
    return isLocked;
  }

  /**
   * 使用lua脚本进行原子释放锁 <br>
   * 解决问题：<br>
   * 如果锁已自动释放请求仍未执行完，则可能会让被其他线程获取到该锁，必须保证不能释放掉别人加的锁 <br>
   * （同时业务逻辑中也要通过乐观锁或其他方式避免并发问题发生） <br>
   */
  private static final RedisScript<Long> UNLOCK_SCRIPT =
      new DefaultRedisScript<>(
          "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('DEL', KEYS[1]) else return 0 end",
          Long.class);

  public void unlock(String name) {
    var op = this.getLock(name);
    if (op.isEmpty()) {
      return;
    }
    // 重入锁次数减一 status仍大于0 则表示未完全释放 直接return
    var lock = op.get();
    if (--lock.status > 0) {
      return;
    }
    // 释放锁
    try {
      var result =
          this.redisTemplate.execute(
              UNLOCK_SCRIPT, Collections.singletonList(name), lock.requestId);
      log.info("Unlock '{}', execute return '{}'.", name, result);
      if (result == null || result < 1) {
        log.warn("Unlock '{}' failed.", name);
      }
    } catch (Exception e) {
      log.warn("Unlock error!", e);
    } finally {
      // 使用future中断任务
      lock.future.cancel(true);
      // 移除Key
      holder.get().remove(name);
    }
  }

  /**
   * watchdog机制 自动延长锁时间 <br>
   * 参考CompletableFuture.Delayer.delayer
   */
  private static final ScheduledThreadPoolExecutor WATCH_DOG =
      new ScheduledThreadPoolExecutor(
          1,
          r -> {
            var t = new Thread(r);
            // 设置为守护线程
            t.setDaemon(true);
            t.setName("RedisDistributedLockDoggy");
            return t;
          });

  static {
    WATCH_DOG.setRemoveOnCancelPolicy(true);
  }

  private static final RedisScript<Long> WATCH_DOG_SCRIPT =
          new DefaultRedisScript<>("""
                  if redis.call('GET', KEYS[1]) == ARGV[1] then
                    if redis.call('SET', KEYS[1], ARGV[1], 'XX', 'PX', ARGV[2]) then
                      return 1
                    else
                      return 0
                    end
                  else
                    return 0
                  end
                  """, Long.class);

  // lock需要通过参数传递过来，无法通过ThreadLocal取出。也不使用TTL了，减少性能消耗。
  private void watching(String name, Lock lock) {
    // 保存或更新future，在释放锁的时候可以中断任务
    // 延迟 2/3 expireMills 执行脚本
    lock.future =
        WATCH_DOG.schedule(
            () -> {
              try {
                // 不需要判断是否释放了锁，也无法判断，因为获取不到原线程ThreadLocal
                var result =
                    this.redisTemplate.execute(
                        WATCH_DOG_SCRIPT,
                        Collections.singletonList(name),
                        lock.requestId,
                        String.valueOf(this.expireMills)); // 使用StringRedisTemplate要求参数必须为String类型
                // 如果延长成功，继续watch
                log.info("Dog watching '{}' execute return '{}'.", name, result);
                if (result != null && result > 0) {
                  watching(name, lock);
                }
              } catch (Exception e) {
                log.warn("Dog watching execute error!", e);
              }
            },
            expireMills * 2 / 3,
            TimeUnit.MILLISECONDS);
  }
}
