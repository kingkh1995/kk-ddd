package com.kkk.op.job.controller;

import com.kkk.op.job.service.JobService;
import com.kkk.op.support.annotation.BaseController;
import com.kkk.op.support.model.command.JobAddCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class JobController {

  @Autowired private JobService jobService;

  @Autowired(required = false) // todo... 自动注入不行，但是ApplicationContext可以获取到Bean。
  @Qualifier("deadJobBootstrap") // 一次性调度作业会自动创建一个OneOffJobBootstrap
  private OneOffJobBootstrap deadJobBootstrap;

  @PostMapping("/job")
  @ResponseStatus(HttpStatus.CREATED)
  public Boolean add(@RequestBody @Validated JobAddCommand jobAddCommand) {
    return jobService.add(jobAddCommand);
  }

  @GetMapping("/deadJob")
  public void executeDeadJob() {
    deadJobBootstrap.execute();
  }
}
