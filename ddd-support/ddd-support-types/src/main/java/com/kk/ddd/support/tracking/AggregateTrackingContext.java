package com.kk.ddd.support.tracking;

import com.kk.ddd.support.base.Aggregate;
import com.kk.ddd.support.marker.Identifier;
import javax.validation.constraints.NotNull;

/**
 * 聚合根context接口（职责仅限管理追踪，由manager对追踪的安全负责）<br>
 *
 * @author KaiKoo
 */
public interface AggregateTrackingContext<T extends Aggregate<ID>, ID extends Identifier> {

  /** 判断是否存在追踪 */
  boolean contains(@NotNull ID id);

  /** 移除追踪 */
  T remove(@NotNull ID id);

  /** 保存追踪 */
  void put(@NotNull T t);

  /** 获取追踪 */
  T get(@NotNull ID id);
}
