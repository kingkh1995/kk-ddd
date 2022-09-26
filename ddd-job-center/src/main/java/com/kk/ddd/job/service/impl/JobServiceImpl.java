package com.kk.ddd.job.service.impl;

import com.kk.ddd.job.domain.JobDAO;
import com.kk.ddd.job.domain.JobDO;
import com.kk.ddd.job.message.JobMessageSender;
import com.kk.ddd.job.service.JobService;
import com.kk.ddd.support.enums.JobStateEnum;
import com.kk.ddd.support.model.command.JobAddCommand;
import com.kk.ddd.support.model.event.JobActionEvent;
import com.kk.ddd.support.model.event.JobReverseEvent;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * todo... 使用sharding-jdbc分表 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  // slot必须为2的幂，则mask为slot-1，使用&运算可以快速得到slot。
  int SLOT_MASK = 7;

  // 最大重试次数
  int MAX_RETRY = 16;

  private static final RedisScript<?> TRANSFER_SCRIPT =
      new DefaultRedisScript<>(
          """
          local max = tonumber(ARGV[1])
          local table = redis.call('ZRANGEBYSCORE', KEYS[1], 0, max)
          for i = 1, #table
          do
            redis.call('HSET', KEYS[2], table[i], 0)
            redis.call('ZREM', KEYS[1], table[i])
          end
          """);

  private final JobDAO jobDAO;

  private final StringRedisTemplate stringRedisTemplate;

  private final JobMessageSender jobMessageSender;

  @Transactional // redis操作失败时回滚数据库
  @Override
  public Boolean add(JobAddCommand addCommand) {
    // 落库
    var jobDO = new JobDO();
    jobDO.setTopic(addCommand.getTopic());
    jobDO.setActionTime(new Date(addCommand.getActionTime()));
    jobDO.setContext(addCommand.getContext());
    jobDO.setState(JobStateEnum.P);
    jobDAO.save(jobDO);
    return add2StoreQueue(jobDO.getId(), jobDO.getActionTime());
  }

  @Transactional // redis操作失败时回滚数据库
  @Override
  public void reverse(JobReverseEvent reverseEvent) {
    if (jobDAO.transferStateById(reverseEvent.getId(), JobStateEnum.D, JobStateEnum.P) > 0) {
      add2StoreQueue(reverseEvent.getId(), reverseEvent.getActionTime());
    }
  }

  // add to StoreQueue(zset)
  private Boolean add2StoreQueue(Long jobId, Date jobActionTime) {
    return stringRedisTemplate
        .opsForZSet()
        .add(
            getStoreQueueSlotKey(jobId.intValue() & SLOT_MASK),
            jobId.toString(),
            (double) jobActionTime.getTime());
  }

  @Override
  public void action(JobActionEvent actionEvent) {
    var slot = actionEvent.getSlot();
    if (slot > SLOT_MASK) {
      return;
    }
    var storeQueueSlotKey = getStoreQueueSlotKey(slot);
    var prepareQueueSlotKey = getPrepareQueueSlotKey(slot);
    var hashOperations = stringRedisTemplate.opsForHash();
    // transfer StoreQueue(zset) to PrepareQueue(hash)
    stringRedisTemplate.execute(
        TRANSFER_SCRIPT,
        List.of(storeQueueSlotKey, prepareQueueSlotKey),
        String.valueOf(System.currentTimeMillis()));
    // get all from PrepareQueue(hash) also compensate
    var futures =
        hashOperations.keys(prepareQueueSlotKey).stream()
            .map(
                o ->
                    CompletableFuture.runAsync(
                        () -> action(o, hashOperations, prepareQueueSlotKey)))
            .toArray(CompletableFuture[]::new);
    // waiting to finish
    CompletableFuture.allOf(futures).join();
  }

  // 因为ElasticJob会保证一个分片在一个时刻只会被一个线程执行，故不需要考虑并发操作问题。
  private void action(
      Object key,
      HashOperations<String, Object, Object> hashOperations,
      String prepareQueueSlotKey) {
    try {
      var jobDO = jobDAO.findById(Long.valueOf((String) key)).orElse(null);
      // 数据不存在或状态不为pending则直接从PrepareQueue删除（理论上不可能发生）
      // 死信处理，因为发送消息失败不会执行任何操作，故在每轮开始前对上一轮的失败的消息进行处理。
      if (Objects.isNull(jobDO)
          || !JobStateEnum.P.equals(jobDO.getState())
          || (hashOperations.increment(prepareQueueSlotKey, key, 1L) > MAX_RETRY
              && jobDAO.transferStateById(jobDO.getId(), JobStateEnum.P, JobStateEnum.D) > 0)) {
        hashOperations.delete(prepareQueueSlotKey, key);
        return;
      }
      // 发送任务消息，并设置本地执行任务。
      jobMessageSender.send(
          jobDO.getTopic(),
          jobDO.getContext(),
          () -> {
            // 消息发送成功则更新任务状态为actioned并从PrepareQueue删除。
            if (jobDAO.transferStateById(jobDO.getId(), JobStateEnum.P, JobStateEnum.A) > 0) {
              hashOperations.delete(prepareQueueSlotKey, key);
              return true;
            }
            return false;
          });
    } catch (Exception e) {
      // 兜底异常处理，防止中断循环。
      log.error("action failed! key:{}, prepareQueueSlotKey:{}.", key, prepareQueueSlotKey, e);
    }
  }

  private String getStoreQueueSlotKey(int slot) {
    // 使用slot作为hash tag
    return "SQ:{" + slot + "}";
  }

  private String getPrepareQueueSlotKey(int slot) {
    // 使用slot作为hash tag
    return "PQ:{" + slot + "}";
  }
}
