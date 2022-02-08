package com.kkk.op.job;

import com.kkk.op.job.persistence.JobDAO;
import com.kkk.op.job.persistence.JobDO;
import com.kkk.op.support.base.Kson;
import com.kkk.op.support.enums.JobStateEnum;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class OpJobCenterApplicationTests {

  @Autowired private JobDAO jobDAO;

  @Test
  void testJpa() {
    JobDO jobDO = new JobDO();
    jobDO.setContext("null");
    jobDO.setBizType("testType");
    jobDO.setActionTime(new Date());
    jobDO.setState(JobStateEnum.P);
    jobDAO.save(jobDO);
    System.out.println(jobDO.getId());
    System.out.println(Kson.writeJson(jobDAO.findAllByState(JobStateEnum.P)));
  }
}
