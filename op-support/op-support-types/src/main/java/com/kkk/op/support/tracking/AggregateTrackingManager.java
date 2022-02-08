package com.kkk.op.support.tracking;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.Identifier;
import com.kkk.op.support.tracking.diff.Diff;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

/**
 * 聚合根追踪更新manager接口 <br>
 * （建议DO类都加上乐观锁，防止并发冲突）
 *
 * @author KaiKoo
 */
public interface AggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier> {

  /** 让一个 Aggregate 变为可追踪（在insert和select操作之后执行，如果已被追踪则返回追踪，相当于保证了可重复读） */
  T attach(@NotNull T aggregate);

  /** 解除一个 Aggregate 的追踪（在delete操作完成之后执行） */
  void detach(@NotNull T aggregate);

  /** 更新一个 Aggregate 的追踪（在update操作完成之后执行） */
  void merge(@NotNull T aggregate);

  /** 获取 Aggregate 变更信息 */
  Diff detectChanges(@NotNull T aggregate);

  /** 获取一个 Aggregate 的快照 （安全副本） */
  @Nullable
  T obtain(@NotNull ID id);
}
