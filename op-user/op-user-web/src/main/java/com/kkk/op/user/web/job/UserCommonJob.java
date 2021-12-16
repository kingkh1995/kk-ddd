package com.kkk.op.user.web.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

/**
 * 分布式定时任务，需要定义到接口层，执行应用层方法。 <br>
 * elasticjob-lite：每台工作服务器都启动一个节点，zk作为注册中心和协调选举，主节点执行任务分片，所有节点均定时执行任务。 <br>
 * 默认分片策略为平均分片，默认启动一个核心cpu数量两倍的线程池执行任务。
 *
 * @author KaiKoo
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UserCommonJob implements SimpleJob {

  @Override
  public void execute(ShardingContext shardingContext) {
    log.info("context: {}", shardingContext);
    // todo...
  }
}
