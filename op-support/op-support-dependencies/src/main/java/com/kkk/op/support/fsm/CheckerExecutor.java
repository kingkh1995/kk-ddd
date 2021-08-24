package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 校验执行器工具类 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class CheckerExecutor {

  /**
   * 执行检查器检查
   *
   * @param checkers 检查器合集（已排序）
   * @param context 被检查上下文信息
   * @param isParallel 是否并行执行
   * @param <E> 事件类型
   * @param <T> 实体类型
   * @param <C> 上下文类型
   * @return
   */
  private static <E extends FsmEvent, T extends Entity, C extends FsmContext<E, T>>
      CheckResult check0(List<Checker<E, T, C>> checkers, C context, boolean isParallel) {
    // 空集合直接返回成功
    if (checkers == null || checkers.isEmpty()) {
      return CheckResult.success();
    }
    // 只有一个直接处理
    if (checkers.size() == 1) {
      return checkers.get(0).check(context);
    }
    // 批量处理，同步或异步（不使用线程池而是使用并行流）
    var stream = checkers.stream();
    if (isParallel) {
      stream.parallel();
    }
    // 使用findAny获取结果，因为并行流findFirst无法断路，且串行流findAny相当于findFirst。
    return stream
        .map(checker -> checker.check(context))
        .filter(CheckResult::isFailed)
        .findAny()
        .orElse(CheckResult.success());
  }

  public static <E extends FsmEvent, T extends Entity, C extends FsmContext<E, T>>
      CheckResult parallelCheck(List<Checker<E, T, C>> checkers, C context) {
    return check0(checkers, context, true);
  }

  public static <E extends FsmEvent, T extends Entity, C extends FsmContext<E, T>>
      CheckResult serialCheck(List<Checker<E, T, C>> checkers, C context) {
    return check0(checkers, context, false);
  }
}
