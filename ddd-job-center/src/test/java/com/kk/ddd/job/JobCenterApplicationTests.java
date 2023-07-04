package com.kk.ddd.job;

import com.kk.ddd.job.domain.JobDAO;
import com.kk.ddd.job.domain.JobDO;
import com.kk.ddd.support.bean.Jackson;
import com.kk.ddd.support.constant.JobStateEnum;
import java.util.Date;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("dev")
class JobCenterApplicationTests {

  @Autowired private JobDAO jobDAO;

  @Autowired private ApplicationContext applicationContext;

  @Transactional
  @Test
  void testJpa() {
    JobDO jobDO = new JobDO();
    jobDO.setContext("null");
    jobDO.setTopic("test");
    jobDO.setActionTime(new Date());
    jobDO.setState(JobStateEnum.P);
    jobDAO.save(jobDO);
    System.out.println(
        Jackson.object2String(jobDAO.findByState(JobStateEnum.P, PageRequest.ofSize(10))));
    System.out.println(jobDAO.transferStateById(jobDO.getId(), JobStateEnum.P, JobStateEnum.D));
    System.out.println(Jackson.object2String(jobDAO.findAllByState(JobStateEnum.P)));
    System.out.println(Jackson.object2String(jobDAO.findAll()));
  }

  @Test
  void testOneOffJobBootstrap() throws Exception {
    applicationContext
        .getBeansOfType(OneOffJobBootstrap.class)
        .values()
        .forEach(OneOffJobBootstrap::execute);
    Thread.sleep(1000);
  }
}
