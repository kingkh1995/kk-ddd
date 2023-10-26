package com.kk.ddd.job.controller;

import com.kk.ddd.job.service.JobService;
import com.kk.ddd.support.annotation.BaseController;
import com.kk.ddd.support.model.command.JobAddCommand;
import com.kk.ddd.support.util.ApplicationContextAwareSingleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Validated
@BaseController
@RequestMapping("/api/v1")
public class JobController extends ApplicationContextAwareSingleton {

  private JobService jobService;

  private OneOffJobBootstrap deadJobBootstrap;

  @Autowired
  public void setJobService(JobService jobService) {
    this.jobService = jobService;
  }

  @Override
  public void afterSingletonsInstantiated() {
    // OneOffJobBootstrap无法被自动注入，设置@Lazy也不行，因为OneOffJobBootstrap是final的
    this.deadJobBootstrap =
        (OneOffJobBootstrap) getApplicationContext().getBean("deadJobBootstrap");
  }

  @PostMapping("/job")
  @ResponseStatus(HttpStatus.CREATED) // fixme... 改为消费消息的方式落库
  public Boolean add(@RequestBody @Validated JobAddCommand jobAddCommand) {
    return jobService.add(jobAddCommand);
  }

  @GetMapping("/deadJob")
  public void executeDeadJob() {
    deadJobBootstrap.execute();
  }
}
