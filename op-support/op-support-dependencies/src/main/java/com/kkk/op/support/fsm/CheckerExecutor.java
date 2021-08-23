package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 校验执行器工具类 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class CheckerExecutor {

  // fixme... 待定义
  public static final ExecutorService DEFAULT =
      Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() - 1);

  /** 执行同步校验 */
  public static <
          E extends FiniteStateMachineEvent,
          T extends Entity,
          C extends FiniteStateMachineContext<E, T>>
      CheckResult serialCheck(List<Checker<E, T, C>> checkers, C context) {
    if (checkers == null || checkers.isEmpty()) {
      return CheckResult.success();
    }
    if (checkers.size() == 1) {
      return checkers.get(0).check(context);
    }
    checkers.sort(Comparator.comparingInt(Checker::order));
    for (var checker : checkers) {
      var result = checker.check(context);
      if (!result.isSuccess()) {
        return result;
      }
    }
    return CheckResult.success();
  }

  /** 执行并行校验器，按照任务投递的顺序判断返回。 */
  public static <
          E extends FiniteStateMachineEvent,
          T extends Entity,
          C extends FiniteStateMachineContext<E, T>>
      CheckResult parallelCheck(
          List<Checker<E, T, C>> checkers, C context, ExecutorService executor) throws Exception {
    if (checkers == null || checkers.isEmpty()) {
      return CheckResult.success();
    }
    // 只有一个直接处理
    if (checkers.size() == 1) {
      return checkers.get(0).check(context);
    }
    // 多个则异步处理
    var futureList =
        checkers.stream()
            .sorted(Comparator.comparingInt(Checker::order))
            .map(checker -> executor.submit(() -> checker.check(context)))
            .collect(Collectors.toUnmodifiableList());
    for (var future : futureList) {
      var result = future.get();
      if (!result.isSuccess()) {
        return result;
      }
    }
    return CheckResult.success();
  }

  /** 执行并行校验器，使用默认线程池，按照任务投递的顺序判断返回。 */
  public static <
          E extends FiniteStateMachineEvent,
          T extends Entity,
          C extends FiniteStateMachineContext<E, T>>
      CheckResult parallelCheck(List<Checker<E, T, C>> checkers, C context) throws Exception {
    return parallelCheck(checkers, context, DEFAULT);
  }
}
