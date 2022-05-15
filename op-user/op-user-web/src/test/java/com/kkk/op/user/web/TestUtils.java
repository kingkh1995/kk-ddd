package com.kkk.op.user.web;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
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
    // 提交async任务
    var futures =
        IntStream.range(0, n)
            .mapToObj(
                i ->
                    CompletableFuture.supplyAsync(
                        () -> {
                          async.accept(i);
                          return i;
                        }))
            .toArray(CompletableFuture[]::new);
    // 等待执行完毕
    CompletableFuture.allOf(futures).join();
    log.info("Async finish!");
    // 提交after任务并等待执行完毕
    CompletableFuture.allOf(
            Arrays.stream(futures)
                .map(CompletableFuture::join)
                .map(i -> CompletableFuture.runAsync(() -> after.accept((int) i)))
                .toArray(CompletableFuture[]::new))
        .join();
    log.info("After finish!");
  }
}
