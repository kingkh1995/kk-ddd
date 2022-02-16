package com.kkk.op.job.component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutor {

  /**
   * 异步执行任务
   *
   * @param topic
   * @param context
   * @return
   */
  public CompletableFuture<Boolean> execute(String topic, String context) {
    log.info("execute job, topic '{}', context '{}'.", topic, context);
    // todo... 直接调用对应的接口或者发送事务消息。
    if (ThreadLocalRandom.current().nextBoolean()) {
      throw new IllegalCallerException();
    }
    return ThreadLocalRandom.current().nextBoolean()
        ? CompletableFuture.completedFuture(false)
        : CompletableFuture.<Boolean>failedStage(new IllegalStateException()).toCompletableFuture();
  }
}
