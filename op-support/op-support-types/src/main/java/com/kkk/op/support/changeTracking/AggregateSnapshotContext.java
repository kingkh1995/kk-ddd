package com.kkk.op.support.changeTracking;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.Identifier;
import javax.validation.constraints.NotNull;

/**
 * 聚合根快照管理context接口 <br>
 * （职责仅限管理快照，由manager对快照的安全负责）
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
   * 保存快照
   *
   * @param snapshot
   */
  void putSnapshot(@NotNull T snapshot);

  /**
   * 获取快照（返回快照原始版本，由manager拷贝并返回副本）
   *
   * @param id
   * @return
   */
  T getSnapshot(@NotNull ID id);
}
