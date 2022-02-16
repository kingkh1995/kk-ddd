package com.kkk.op.job.service.impl;

import com.kkk.op.job.component.JobExecutor;
import com.kkk.op.job.persistence.JobDAO;
import com.kkk.op.job.persistence.JobDO;
import com.kkk.op.job.service.JobService;
import com.kkk.op.support.base.Kson;
import com.kkk.op.support.enums.JobStateEnum;
import com.kkk.op.support.model.command.JobAddCommand;
import com.kkk.op.support.model.event.JobActionEvent;
import com.kkk.op.support.model.event.JobReverseEvent;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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

  // REQUIRES_NEW为额外开启新事务，与外部事务互不影响，而NESTED会被外部事务影响但不会影响外部事务。
  private static final TransactionDefinition TD =
      new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

  private static final RedisScript<List> TRANSFER_SCRIPT = new DefaultRedisScript<>("""
          local max = tonumber(ARGV[1])
          local table = redis.call('ZRANGEBYSCORE', KEYS[1], 0, max)
          for i = 1, #table
          do
            redis.call('HSET', KEYS[2], table[i], 0)
            redis.call('ZREM', KEYS[1], table[i])
          end
          return table
          """, List.class);

  private final JobDAO jobDAO;

  private final StringRedisTemplate stringRedisTemplate;

  private final PlatformTransactionManager transactionManager;

  private final JobExecutor jobExecutor;

  @Transactional
  @Override
  public Boolean add(JobAddCommand addCommand) {
    // 落库
    var jobDO = new JobDO();
    jobDO.setTopic(addCommand.getTopic());
    jobDO.setActionTime(new Date(addCommand.getActionTime()));
    jobDO.setContext(addCommand.getContext());
    jobDO.setState(JobStateEnum.P);
    jobDAO.save(jobDO);
    // push to StoreQueue(Zset)
    return stringRedisTemplate
        .opsForZSet()
        .add(
            getStoreQueueSlotKey(jobDO.getId().intValue() & SLOT_MASK),
            jobDO.getId().toString(),
            (double) jobDO.getActionTime().getTime());
  }

  @Override
  public void action(JobActionEvent actionEvent) {
    var slot = actionEvent.getSlot();
    if (slot > SLOT_MASK) {
      return;
    }
    var storeQueueSlotKey = getStoreQueueSlotKey(slot);
    var prepareQueueSlotKey = getPrepareQueueSlotKey(slot);
    // compensate before transfer，get all from PrepareQueue(Set)
    stringRedisTemplate
        .opsForHash()
        .keys(getPrepareQueueSlotKey(slot))
        .forEach(o -> action(o, prepareQueueSlotKey));
    // transfer StoreQueue(Zset) to PrepareQueue(Set)
    stringRedisTemplate
        .execute(
            TRANSFER_SCRIPT,
            List.of(storeQueueSlotKey, prepareQueueSlotKey),
            String.valueOf(System.currentTimeMillis()))
        .forEach(o -> action(o, prepareQueueSlotKey));
  }

  @Transactional
  @Override
  public void reverse(JobReverseEvent reverseEvent) {
    if (jobDAO.updateStateByIdAndState(JobStateEnum.P, reverseEvent.getId(), JobStateEnum.D) > 0) {
      stringRedisTemplate
          .opsForZSet()
          .add(
              getStoreQueueSlotKey(reverseEvent.getId().intValue() & SLOT_MASK),
              reverseEvent.getId().toString(),
              (double) reverseEvent.getActionTime());
    }
  }

  private void action(Object key, String prepareQueueSlotKey) {
    if (key instanceof String s) {
      try {
        var operations = stringRedisTemplate.opsForHash();
        var jobDO = jobDAO.findById(Long.valueOf(s)).orElse(null);
        // 数据不存在或状态不为pending则直接从PrepareQueue删除（理论上不可能发生）
        if (Objects.isNull(jobDO) || !JobStateEnum.P.equals(jobDO.getState())) {
          operations.delete(prepareQueueSlotKey, key);
          log.warn("This shouldn't happen! job:'{}'.", Kson.writeJson(jobDO));
          return;
        }
        jobExecutor
            .execute(jobDO.getTopic(), jobDO.getContext())
            // whenComplete表示由异步执行线程自身执行回调，whenCompleteAsync则是重新获取一个线程（即异步）去执行回调。
            .whenCompleteAsync(
                (isSucceeded, throwable) -> {
                  // 开启一个新事务
                  var transaction = transactionManager.getTransaction(TD);
                  try {
                    if (Boolean.TRUE.equals(isSucceeded)) {
                      // 执行成功则更新任务状态为actioned并从PrepareQueue删除。
                      if (jobDAO.updateStateByIdAndState(
                              JobStateEnum.A, jobDO.getId(), JobStateEnum.P)
                          > 0) {
                        operations.delete(prepareQueueSlotKey, key);
                      }
                    } else {
                      // 失败或抛出异常则count++，如果达到上限则更新任务状态为dead并从PrepareQueue删除。
                      log.warn(
                          "job execute failed! job:'{}', isSucceeded:'{}'.",
                          Kson.writeJson(jobDO),
                          isSucceeded,
                          throwable);
                      // 因为elasticjob会保证一个分片在一个时刻只会被一个线程执行，故不需要考虑并发操作问题。
                      if (operations.increment(prepareQueueSlotKey, key, 1L) >= MAX_RETRY
                          && jobDAO.updateStateByIdAndState(
                                  JobStateEnum.D, jobDO.getId(), JobStateEnum.P)
                              > 0) {
                        operations.delete(prepareQueueSlotKey, key);
                      }
                    }
                    // 为保证数据库和redis的原子性，redis操作成功才提交本次事务。
                    transactionManager.commit(transaction);
                  } catch (Exception e) {
                    log.error("job execute callback failed!", e);
                    // 异常时事务回滚
                    transactionManager.rollback(transaction);
                  }
                });
      } catch (Exception e) {
        // 兜底异常，防止中断循环。
        log.error("action failed!", e);
      }
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
