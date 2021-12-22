package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.tool.SleepHelper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * redis多重可重入锁（lua脚本实现）<br>
 * fixme... 只能用于单机模式，因为无法将键全部映射到同一个slot。
 *
 * @author KaiKoo
 */
@Slf4j
public class RedisMultiLock implements DistributedLock {

  private final List<String> names;

  private final RedisDistributedLockFactory factory;

  public RedisMultiLock(
      @Size(min = 2) List<String> names, @NotNull RedisDistributedLockFactory factory) {
    this.names = List.copyOf(names);
    this.factory = factory;
  }

  @Override
  public boolean tryLock(long waitSeconds) {
    return SleepHelper.tryGetThenSleep(
        this::tryLock0, TimeUnit.SECONDS.toMillis(waitSeconds), this.factory.getSpinInterval());
  }

  // lua脚本返回数组需要使用List类型接收，元素如果是整数则默认是Long类型
  private static final RedisScript<List> LOCK_SCRIPT = new DefaultRedisScript<>("""
          local list = {}
          local seq = ARGV[1]
          -- 遍历判断是否能获取锁
          for i = 1, #KEYS
          do
              local lseq = redis.call('GET', KEYS[i])
              -- 键不存在时需要使用false判断
              if lseq == false then
                  -- 锁未被获取
                  list[i] = 1
              elseif lseq ~= seq then
                  -- 锁已被其他线程获取
                  return nil
              end
          end
          -- 执行操作
          for i = 1, #KEYS
          do
              local ckey = KEYS[i] .. seq
              if list[i] == 1 then
                  -- 初次获取锁
                  redis.call('SET', KEYS[i], seq, 'PX', ARGV[2])
                  redis.call('SET', ckey, 1)
              else
                  -- 锁重入
                  list[i] = redis.call('INCR', ckey)
              end
          end
          -- 返回所有锁的状态
          return list
          """, List.class);

  private boolean tryLock0() {
    var seq = RedisDistributedLockFactory.getSeq();
    var result =
        this.factory
            .getRedisTemplate()
            .execute(LOCK_SCRIPT, this.names, seq, String.valueOf(this.factory.getExpireMills()));
    log.info("Lock '{}' use '{}' return '{}'.", this.names, seq, result);
    if (result == null) {
      // 结果为null 获取锁失败
      return false;
    }
    // 判断是否需要开启watch
    for (var i = 0; i < result.size(); i++) {
      // 当前元素为1表示初次获取锁成功
      if(result.get(i) instanceof Long l && l == 1){
        this.factory.watch(this.names.get(i), seq);
      }
    }
    return true;
  }

  private static final RedisScript<List> UNLOCK_SCRIPT = new DefaultRedisScript<>("""
          local list = {}
          local seq = ARGV[1]
          for i = 1, #KEYS
          do
              if redis.call('GET', KEYS[i]) == seq then
                  local ckey = KEYS[i] .. seq
                  local count = redis.call('DECR', ckey)
                  -- 如果加锁次数减少为0则删除锁信息
                  if count <= 0 then
                      redis.call('DEL', KEYS[i], ckey)
                  end
                  -- 返回当前加锁次数
                  list[i] = count
              else
                  list[i] = -1
              end
          end
          return list
          """, List.class);

  @Override
  public void unlock() {
    var seq = RedisDistributedLockFactory.getSeq();
    var result = this.factory.getRedisTemplate().execute(UNLOCK_SCRIPT, this.names, seq);
    log.info("Unlock '{}' use '{}' return '{}'.", this.names, seq, result);
    // 判断是否需要关闭watch
    if (result != null) {
      for (var i = 0; i < result.size(); i++) {
        // 当前元素为0表示锁被完全释放
        if(result.get(i) instanceof Long l && l == 0){
          this.factory.cancelWatching(this.names.get(i));
        }
      }
    }
  }
}
