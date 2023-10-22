package com.kk.ddd.job.controller;

import com.kk.ddd.job.service.JobService;
import com.kk.ddd.support.annotation.BaseController;
import com.kk.ddd.support.model.command.JobAddCommand;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class JobController implements ApplicationContextAware, CommandLineRunner {

  private JobService jobService;

  @Setter private ApplicationContext applicationContext;
  private OneOffJobBootstrap deadJobBootstrap;

  @Autowired
  public void setJobService(JobService jobService) {
    this.jobService = jobService;
  }

  /*@Autowired
  @Qualifier("deadJobBootstrap") // 一次性调度作业会自动创建一个OneOffJobBootstrap
  public void setDeadJobBootstrap(OneOffJobBootstrap deadJobBootstrap) {
    this.deadJobBootstrap = deadJobBootstrap;
  }*/

  @Override
  public void run(String... args) throws Exception { // 或org.springframework.boot.ApplicationRunner
    this.deadJobBootstrap =
        (OneOffJobBootstrap) this.applicationContext.getBean("deadJobBootstrap");
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
