package com.kkk.op.support.fsm;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;

/**
 * 校验执行器工具类 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class CheckerExecutor {

  /**
   * 执行校验器校验
   *
   * @param checkers 校验器合集
   * @param context 被校验上下文信息
   * @param isParallel 是否并行执行
   * @param <E> 事件类型
   * @param <T> 实体类型
   * @param <C> 上下文类型
   * @return
   */
  private static <E extends FsmEvent, T, C extends FsmContext<E, T>> CheckResult check0(
      Collection<Checker<E, T, C>> checkers, C context, boolean isParallel) {
    // 空集合直接返回成功
    if (checkers == null || checkers.isEmpty()) {
      return CheckResult.succeed();
    }
    // 数量为1则直接执行
    if (checkers.size() == 1) {
      return checkers.iterator().next().check(context);
    }
    // 批量处理，同步或异步（不使用线程池而是使用并行流）
    var stream = isParallel ? checkers.parallelStream() : checkers.stream();
    // 需要使用findAny，因为并行流findFirst无法断路，且串行流findAny相当于findFirst。
    return stream
        .map(checker -> checker.check(context))
        .filter(Predicate.not(CheckResult::succeeded))
        .findAny()
        .orElse(CheckResult.succeed());
  }

  public static <E extends FsmEvent, T, C extends FsmContext<E, T>> CheckResult serialCheck(
      List<Checker<E, T, C>> checkers, C context) {
    return check0(checkers, context, false);
  }

  public static <E extends FsmEvent, T, C extends FsmContext<E, T>> CheckResult parallelCheck(
      Collection<Checker<E, T, C>> checkers, C context) {
    return check0(checkers, context, true);
  }
}
