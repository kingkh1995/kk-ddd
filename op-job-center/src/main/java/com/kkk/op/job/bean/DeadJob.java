package com.kkk.op.job.bean;

import com.kkk.op.job.persistence.JobDAO;
import com.kkk.op.job.persistence.JobDO;
import com.kkk.op.job.service.JobService;
import com.kkk.op.support.enums.JobStateEnum;
import com.kkk.op.support.model.event.JobReverseEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * DataflowJob可开启流式处理（配置属性streaming.process），会顺序循环处理直到fetchData无数据返回。 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeadJob implements DataflowJob<JobDO> {

  private final JobDAO jobDAO;

  private final JobService jobService;

  @Override
  public List<JobDO> fetchData(ShardingContext shardingContext) {
    // 每次限制100条数据
    return jobDAO.findByState(JobStateEnum.D, Pageable.ofSize(100)).getContent();
  }

  @Override
  public void processData(ShardingContext shardingContext, List<JobDO> data) {
    log.info("({})::process data, size:{}.", shardingContext.getTaskId(), data.size());
    data.forEach(
        jobDO ->
            jobService.reverse(
                new JobReverseEvent()
                    .setId(jobDO.getId())
                    .setActionTime(jobDO.getActionTime().getTime())));
  }
}
