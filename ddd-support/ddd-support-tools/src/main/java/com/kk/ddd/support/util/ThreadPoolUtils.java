package com.kk.ddd.support.util;

import com.alibaba.ttl.TtlRunnable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池工具类 <br>
 *
 * @author KaiKoo
 */
public final class ThreadPoolUtils {

  private ThreadPoolUtils() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final ConcurrentMap<String, ThreadPoolTaskExecutor> HOLDER =
      new ConcurrentHashMap<>();

  public static Optional<ExecutorService> get(String name) {
    return Optional.ofNullable(HOLDER.get(name)).map(ThreadPoolTaskExecutor::getThreadPoolExecutor);
  }

  public static ExecutorService getOrInit(String name) {
    return getOrInit(
        name, Constants.EXECUTOR.defaultPoolSize(), Constants.EXECUTOR.defaultPoolSize());
  }

  public static ExecutorService getOrInit(String name, int coreSize, int poolSize) {
    return getOrInit(
        name,
        Constants.EXECUTOR.defaultRejectedPolicy(),
        coreSize,
        poolSize,
        Constants.EXECUTOR.defaultKeepAliveSeconds(),
        Constants.EXECUTOR.defaultQueueCapacity(),
        Constants.EXECUTOR.defaultAllowCoreThreadTimeOut());
  }

  public static ExecutorService getOrInit(
      String name,
      RejectedExecutionHandler rejectedExecutionHandler,
      int coreSize,
      int poolSize,
      int keepAliveSeconds,
      int queueCapacity,
      boolean allowCoreThreadTimeOut) {
    return HOLDER
        .computeIfAbsent(
            name,
            k ->
                createThreadPoolTaskExecutor(
                    name,
                    rejectedExecutionHandler,
                    coreSize,
                    poolSize,
                    keepAliveSeconds,
                    queueCapacity,
                    allowCoreThreadTimeOut))
        .getThreadPoolExecutor();
  }

  private static ThreadPoolTaskExecutor createThreadPoolTaskExecutor(
      String name,
      RejectedExecutionHandler rejectedExecutionHandler,
      int coreSize,
      int poolSize,
      int keepAliveSeconds,
      int queueCapacity,
      boolean allowCoreThreadTimeOut) {
    var taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setBeanName(name);
    taskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
    taskExecutor.setTaskDecorator(TtlRunnable::get); // ttl
    taskExecutor.setCorePoolSize(coreSize);
    taskExecutor.setMaxPoolSize(poolSize);
    taskExecutor.setKeepAliveSeconds(keepAliveSeconds);
    taskExecutor.setQueueCapacity(queueCapacity);
    taskExecutor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
    taskExecutor.initialize();
    return taskExecutor;
  }
}
