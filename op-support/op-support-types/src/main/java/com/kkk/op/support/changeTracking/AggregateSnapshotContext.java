package com.kkk.op.support.changeTracking;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.Identifier;
import javax.validation.constraints.NotNull;

/**
 * 聚合根快照管理context接口 <br>
 * （需要保证外界无法影响到快照的生命周期，对快照的修改封闭）
 *
 * @author KaiKoo
 */
public interface AggregateSnapshotContext<T extends Aggregate<ID>, ID extends Identifier> {

  /**
   * 判断是否存在快照
   *
   * @param id
   * @return
   */
  boolean existSnapshot(@NotNull ID id);

  /**
   * 移除快照
   *
   * @param id
   * @return
   */
  T removeSnapshot(@NotNull ID id);

  /**
   * 不返回put进去的快照
   *
   * @param aggregate
   */
  void putSnapshot(@NotNull T aggregate);

  /**
   * 获取快照（需要返回副本，防止快照被外部修改）
   *
   * @param id
   * @return
   */
  T getSnapshot(@NotNull ID id);
}
