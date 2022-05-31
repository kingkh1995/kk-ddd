package com.kk.ddd.job.service;

import com.kk.ddd.support.model.command.JobAddCommand;
import com.kk.ddd.support.model.event.JobActionEvent;
import com.kk.ddd.support.model.event.JobReverseEvent;
import org.springframework.validation.annotation.Validated;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface JobService {

  /**
   * 添加作业
   *
   * @param addCommand
   * @return
   */
  Boolean add(@Validated JobAddCommand addCommand);

  /**
   * 死作业反转（todo...优化）
   *
   * @param reverseEvent
   */
  void reverse(@Validated JobReverseEvent reverseEvent);

  /**
   * 执行作业调度（有重复调度的可能）
   *
   * @param actionEvent
   */
  void action(@Validated JobActionEvent actionEvent);
}
