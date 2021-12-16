package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.DistributedLockFactory;
import com.kkk.op.support.marker.NameGenerator;
import com.kkk.op.support.tool.SleepHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * 基于redis的分布式锁实现 （适用于redis单机模式，或者对可用性要求不是特别高） <br>
 * 主从模式redis下有一个明显的竞争条件，因为复制是异步的，客户端A在master节点拿到了锁，master节点在把A创建的key写入slave之前宕机了  <br>
 * 基于lua脚本，使用一个键保存锁信息，一个键保存加锁次数。
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
public class RedisDistributedLockFactory implements DistributedLockFactory {

  @Getter private final StringRedisTemplate redisTemplate;

  @Getter @Default private long spinInterval = 200L;

  @Getter @Default private long expireMills = 10L * 1000L;

  /** watchdog机制 自动延长锁时间 <br> */
  @Default private ScheduledExecutorService watchDog = defaultDoggy();

  private static ScheduledExecutorService defaultDoggy() {
    // 参考CompletableFuture.Delayer.delayer
    var doggy =
        new ScheduledThreadPoolExecutor(
            1,
            r -> {
              var t = new Thread(r);
              // 设置为守护线程
              t.setDaemon(true);
              t.setName("RedisDistributedLockDefaultDoggy");
              return t;
            });
    doggy.setRemoveOnCancelPolicy(true);
    return doggy;
  }

  // 供builder使用
  private RedisDistributedLockFactory(
      StringRedisTemplate redisTemplate,
      long spinInterval,
      long expireMills,
      ScheduledExecutorService watchDog) {
    this.redisTemplate = Objects.requireNonNull(redisTemplate);
    if (spinInterval <= 0) {
      throw new IllegalArgumentException(
          "RedisDistributedLock spinInterval should be greater than 0!");
    }
    this.spinInterval = spinInterval;
    if (expireMills <= 0) {
      throw new IllegalArgumentException(
          "RedisDistributedLock expireMills should be greater than 0!");
    }
    this.expireMills = expireMills;
    this.watchDog = Objects.requireNonNull(watchDog);
  }

  @Override
  public NameGenerator getLockNameGenerator() {
    return NameGenerator.joiner(":", "lock:", "");
  }

  private static final String ID = ":" + UUID.randomUUID().toString().replace("-", "").substring(20) + ":";

  public static String getSeq() {
    return ID + Thread.currentThread().getId();
  }

  @Override
  public DistributedLock getLock(String name) {
    return new Lock(name, this);
  }

  @Override
  public DistributedLock getMultiLock(List<String> names) {
    return new RedisMultiLock(names, this);
  }

  private final Map<String, Bone> bowl = new ConcurrentHashMap<>();

  private static class Bone {
    private final String name;
    private final String seq;
    private Future<?> future;

    private Bone(String name, String seq) {
      this.name = name;
      this.seq = seq;
    }
  }

  private static final RedisScript<Boolean> WATCH_SCRIPT = new DefaultRedisScript<>("""
          if redis.call('GET', KEYS[1]) == KEYS[2] then
              return redis.call('SET', KEYS[1], KEYS[2], 'PX', ARGV[1])
          else
              return false
          end
          """, Boolean.class);

  public void watch(String name, String seq) {
    var bone = new Bone(name, seq);
    if (this.bowl.putIfAbsent(name, bone) == null) {
      log.info("Doggy start watching '{}'!", name);
    } else {
      log.warn("Doggy has watched '{}'!", name);
      throw new IllegalCallerException();
    }
    log.info("Currently holding {} bones.", this.bowl.size());
    watch0(bone);
  }

  public void watch0(Bone bone) {
    // 延迟 2/3 expireMills 执行脚本
    bone.future =
        this.watchDog.schedule(
            () -> {
              try {
                var result =
                    this.redisTemplate.execute(
                        WATCH_SCRIPT,
                        Arrays.asList(bone.name, bone.seq),
                        String.valueOf(this.expireMills));
                log.info(
                    "Doggy watching '{}' using '{}' execute return '{}'.",
                    bone.name,
                    bone.seq,
                    result);
                // 如果延长成功，继续watch
                if (result != null && result) {
                  watch0(bone);
                }
              } catch (Exception e) {
                log.warn("Dog watching execute error!", e);
              }
            },
            this.expireMills * 2 / 3,
            TimeUnit.MILLISECONDS);
  }

  public void cancelWatching(String name) {
    this.bowl.computeIfPresent(
        name,
        (s, bone) -> {
          var cancelled = bone.future.cancel(true);
          log.info("Cancel watching '{}' return '{}'.", name, cancelled);
          // 返回null表示移除键
          return null;
        });
    log.info("Currently holding {} bones.", this.bowl.size());
  }

  @AllArgsConstructor
  private static class Lock implements DistributedLock {

    private final String name;

    private final RedisDistributedLockFactory factory;

    @Override
    public boolean tryLock(long waitSeconds) {
      return SleepHelper.tryGetThenSleep(
          this::tryLock0, TimeUnit.SECONDS.toMillis(waitSeconds), this.factory.spinInterval);
    }

    private static final RedisScript<Long> LOCK_SCRIPT = new DefaultRedisScript<>("""
            local seq = redis.call('GET', KEYS[1])
            local ckey = KEYS[1] .. KEYS[2]
            -- 键不存在时需要使用false判断
            if seq == false then
                -- 初次获取锁
                redis.call('SET', KEYS[1], KEYS[2], 'PX', ARGV[1])
                redis.call('SET', ckey, 1)
                return 1
            elseif seq == KEYS[2] then
                -- 锁重入
                return redis.call('INCR', ckey)
            else
                return 0
            end
            """, Long.class);

    private boolean tryLock0() {
      var seq = getSeq();
      // 使用StringRedisTemplate执行lua脚本时要求参数必须为String类型
      var result =
          this.factory.redisTemplate.execute(
              LOCK_SCRIPT, Arrays.asList(this.name, seq), String.valueOf(this.factory.expireMills));
      log.info("Lock '{}' use '{}' return '{}'.", this.name, seq, result);
      if (result == null || result == 0) {
        // 结果为0 获取锁失败
        return false;
      } else if (result == 1) {
        // 结果为1 初次获取锁成功 开启watchdog
        this.factory.watch(this.name, seq);
        return true;
      } else {
        // 结果大于1 锁重入成功
        return true;
      }
    }

    private static final RedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>("""
          if redis.call('GET', KEYS[1]) == KEYS[2] then
              local ckey = KEYS[1] .. KEYS[2]
              local count = redis.call('DECR', ckey)
              -- 如果加锁次数减少为0则删除锁信息
              if count <= 0 then
                  redis.call('DEL', KEYS[1], ckey)
              end
              -- 返回当前加锁次数
              return count
          else
              return -1
          end
          """, Long.class);

    @Override
    public void unlock() {
      var seq = getSeq();
      var result = this.factory.redisTemplate.execute(UNLOCK_SCRIPT, Arrays.asList(this.name, seq));
      log.info("Unlock '{}' use '{}' return '{}'.", this.name, seq, result);
      // result大于0表示锁仍然被持有，等于0表示锁完全释放，小于0为异常情况
      if (result != null && result == 0) {
        // 锁被完全释放则取消watch
        this.factory.cancelWatching(this.name);
      }
    }
  }
}
