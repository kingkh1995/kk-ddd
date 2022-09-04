package com.kk.ddd.support.util;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试工具类 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public final class TestUtils {

  public static void forAsyncTest(int n, IntConsumer async, IntConsumer after) {
    forAsyncTest(n, async, after, ForkJoinPool.commonPool());
  }

  public static void forAsyncTest(int n, IntConsumer async, IntConsumer after, Executor executor) {
    // 提交async任务
    var futures =
        IntStream.range(0, n)
            .mapToObj(
                i ->
                    CompletableFuture.supplyAsync(
                        () -> {
                          async.accept(i);
                          return i;
                        },
                        executor))
            .toArray(CompletableFuture[]::new);
    // 等待执行完毕
    CompletableFuture.allOf(futures).join();
    log.info("Async finish!");
    if (after == null) {
      return;
    }
    // 提交after任务并等待执行完毕
    CompletableFuture.allOf(
            Arrays.stream(futures)
                .map(CompletableFuture::join)
                .map(i -> CompletableFuture.runAsync(() -> after.accept((int) i), executor))
                .toArray(CompletableFuture[]::new))
        .join();
    log.info("After finish!");
  }
}
