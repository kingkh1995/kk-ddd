package com.kk.ddd.support.constant;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface ExecutorConstantsProvider {

  default int defaultPoolSize() {
    return Runtime.getRuntime().availableProcessors();
  }

  default long defaultKeepAliveSeconds() {
    return 300L;
  }

  default int defaultQueueCapacity() {
    return 2048;
  }

  RejectedExecutionHandler DEFAULT_REJECTED_POLICY = new CallerRunsPolicy();

  default RejectedExecutionHandler defaultRejectedPolicy() {
    return DEFAULT_REJECTED_POLICY;
  }
}
