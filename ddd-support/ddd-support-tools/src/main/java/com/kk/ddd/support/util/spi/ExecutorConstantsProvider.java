package com.kk.ddd.support.util.spi;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface ExecutorConstantsProvider {

  RejectedExecutionHandler DEFAULT_REJECTED_POLICY = new CallerRunsPolicy();

  default RejectedExecutionHandler defaultRejectedPolicy() {
    return DEFAULT_REJECTED_POLICY;
  }

  default int defaultPoolSize() {
    return Runtime.getRuntime().availableProcessors();
  }

  default int defaultKeepAliveSeconds() {
    return 30 * 60;
  }

  default int defaultQueueCapacity() {
    return 2048;
  }

  default boolean defaultAllowCoreThreadTimeOut() {
    return false;
  }
}
