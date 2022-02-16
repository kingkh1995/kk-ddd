package com.kkk.op.job.bean;

import com.kkk.op.job.service.JobService;
import com.kkk.op.support.model.event.JobActionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

/**
 * 分布式定时任务，需要定义到接口层，执行应用层方法。 <br>
 * elasticjob-lite：每台工作服务器都启动一个节点，zk作为注册中心和协调选举，主节点执行分片分配，有分配到分片的所有节点均定时执行任务。 <br>
 * 默认分片策略为平均分片，默认启动一个核心cpu数量两倍的线程池执行任务，同时保证了每个分片在同一时刻只会有一个线程在执行。<br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Job implements SimpleJob {

  private final JobService jobService;

  @Override
  public void execute(ShardingContext shardingContext) {
    log.info(
        "start execute::{}/{}({})",
        shardingContext.getShardingItem(),
        shardingContext.getShardingTotalCount(),
        shardingContext.getTaskId());
    jobService.action(new JobActionEvent().setSlot(shardingContext.getShardingItem()));
    log.info(
        "finish execute::{}/{}({})",
        shardingContext.getShardingItem(),
        shardingContext.getShardingTotalCount(),
        shardingContext.getTaskId());
  }
}
