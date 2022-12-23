package com.kk.ddd.support.bean;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.kk.ddd.support.constant.Constants;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 线程池工具类 <br>
 *
 * @author KaiKoo
 */
public final class ThreadPoolManager {

  private ThreadPoolManager() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final Map<String, ExecutorService> HOLDER = new ConcurrentHashMap<>();

  private static ExecutorService createThreadPool(int coreSize, int poolSize, String name) {
    return TtlExecutors.getTtlExecutorService(
        new ThreadPoolExecutor(
            coreSize,
            poolSize,
            Constants.EXECUTOR.defaultKeepAliveSeconds(),
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(Constants.EXECUTOR.defaultQueueCapacity()),
            new DefaultThreadFactory(name),
            Constants.EXECUTOR.defaultRejectedPolicy()));
  }

  public static ExecutorService getOrInit(String name) {
    return HOLDER.computeIfAbsent(
        name,
        k ->
            createThreadPool(
                Constants.EXECUTOR.defaultPoolSize(), Constants.EXECUTOR.defaultPoolSize(), k));
  }

  public static ExecutorService getOrInit(
      String name, @Positive int coreSize, @Positive int poolSize) {
    return HOLDER.computeIfAbsent(name, key -> createThreadPool(coreSize, poolSize, key));
  }

  public static ExecutorService getOrInit(String name, @NotNull ExecutorService executorService) {
    return HOLDER.computeIfAbsent(name, key -> executorService);
  }

  public static Optional<ExecutorService> get(String name) {
    return Optional.ofNullable(HOLDER.get(name));
  }
}
