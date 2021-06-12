package com.kkk.op.support.bean;

import com.kkk.op.support.marker.DistributedLock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class RedisDistributedLock implements DistributedLock {

  @Default private long sleepInterval = 1L << 8;

  @Default private long expireMills = 10L * 1000L;

  private final StringRedisTemplate redisTemplate;

  // ThreadLocal使用时尽量用static修饰
  private static final ThreadLocal<Map<String, LockInfo>> LOCK_CONTEXT =
      ThreadLocal.withInitial(HashMap::new);

  // 供builder使用
  private RedisDistributedLock(
      long sleepInterval, long expireMills, StringRedisTemplate redisTemplate) {
    this.sleepInterval = sleepInterval;
    this.expireMills = expireMills;
    this.redisTemplate = Objects.requireNonNull(redisTemplate);
  }

  private static class LockInfo {

    private final String requestId = UUID.randomUUID().toString();

    private int count = 1;

    private Future<?> future;
  }

  private LockInfo getLockInfo(String name) {
    return LOCK_CONTEXT.get().get(name);
  }

  private boolean lock(String name, String requestId) {
    var result =
        this.redisTemplate
            .opsForValue()
            .setIfAbsent(name, requestId, this.expireMills, TimeUnit.MILLISECONDS);
    return result != null && result;
  }

  /** 获取锁，失败则自旋重试 */
  @Override
  public boolean tryLock(@NotBlank String name, long waitTime, @NotNull TimeUnit unit) {
    // 可重入锁，判断该线程是否获取到了锁
    var lockInfo = this.getLockInfo(name);
    // 已获取到锁则次数加一
    if (lockInfo != null) {
      lockInfo.count++;
      return true;
    }
    // 获取锁
    var waitMills = unit.toMillis(waitTime);
    lockInfo = new LockInfo();
    var locked = this.lock(name, lockInfo.requestId);
    // 获取失败则sleep一段时间再次获取，直到总休眠时间超过waitTime
    for (var i = 0; !locked && waitMills > 0; i++) {
      try {
        var interval = generateSleepMills(i, this.sleepInterval);
        Thread.sleep(interval);
        waitMills -= interval;
      } catch (InterruptedException e) {
        log.error(String.format("线程【%s】睡眠被中断！", Thread.currentThread().getName()), e);
      }
      locked = this.lock(name, lockInfo.requestId);
    }
    if (locked) {
      // 获取到锁 则保存锁信息
      LOCK_CONTEXT.get().put(name, lockInfo);
      // 开启watchdog
      watching(name, lockInfo);
    }
    return locked;
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

  public void unlock(@NotBlank String name) {
    var lockInfo = this.getLockInfo(name);
    if (lockInfo == null) {
      return;
    }
    // 重入锁次数减一 count仍大于0 则表示未完全释放 直接return
    if (--lockInfo.count > 0) {
      return;
    }
    // 释放锁
    try {
      var result =
          this.redisTemplate.execute(
              UNLOCK_SCRIPT, Collections.singletonList(name), lockInfo.requestId);
      if (result == null || result < 1) {
        log.warn("unlock [{}] execute return [{}]", name, result);
      }
    } catch (Exception e) {
      log.warn("unlock error!", e);
    } finally {
      var lockInfoMap = LOCK_CONTEXT.get();
      // 使用future中断任务
      lockInfoMap.get(name).future.cancel(true);
      // 移除Key
      lockInfoMap.remove(name);
    }
  }

  /** watchdog机制 自动延长锁时间 */

  // todo... 使用ThreadPoolExecutor构造方法创建
  private static final ScheduledExecutorService WATCH_DOG =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            // 设置为守护线程
            var t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
          });

  private static final RedisScript<Long> WATCH_DOG_SCRIPT =
      new DefaultRedisScript<>(
          "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('SET', KEYS[1], ARGV[1], 'XX', 'PX', ARGV[2]) else return 0 end",
          Long.class);

  // lockInfo必须通过参数传递过来，不能从ThreadLocal中取
  private void watching(String name, LockInfo lockInfo) {
    // 保存或替换掉future 用于在释放锁的时候中断任务
    // 延迟 expireMills * 9 / 10 时间执行脚本
    lockInfo.future =
        WATCH_DOG.schedule(
            () -> {
              try {
                // 不需要判断是否释放了锁，也无法判断
                var result =
                    this.redisTemplate.execute(
                        WATCH_DOG_SCRIPT,
                        Collections.singletonList(name),
                        lockInfo.requestId,
                        this.expireMills);
                // 如果延长成功，继续watch
                if (result != null && result > 0) {
                  watching(name, lockInfo);
                } else {
                  log.warn("watching [{}] execute return [{}]", name, result);
                }
              } catch (Exception e) {
                log.warn("watch dog error!", e);
              }
            },
            expireMills * 9 / 10,
            TimeUnit.MILLISECONDS);
  }
}
