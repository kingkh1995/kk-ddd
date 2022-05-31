package com.kk.ddd.support.tracking;

import com.kk.ddd.support.base.Aggregate;
import com.kk.ddd.support.marker.Identifier;
import com.kk.ddd.support.tracking.diff.Diff;
import com.kk.ddd.support.tracking.diff.DiffUtil;
import javax.validation.constraints.NotNull;

/**
 * 对外提供追踪变更的功能，内部定义好追踪管理的方式，快照的管理交由AggregateSnapshotContext去实现 <br>
 * 参考CacheManager设计，可以增加其他的接口实现来拓展功能 <br>
 *
 * @author KaiKoo
 */
public abstract class AbstractAggregateTrackingManager<
        T extends Aggregate<ID>, ID extends Identifier>
    implements AggregateTrackingManager<T, ID> {

  protected abstract AggregateTrackingContext<T, ID> getContext();

  /** 拍摄快照 */
  protected abstract T snapshoot(@NotNull T t);

  @Override
  public T attach(T aggregate) {
    // 存在则返回追踪
    var obtained = this.obtain(aggregate.getId());
    if (null != obtained) {
      return obtained;
    }
    // 不存在则使用merge
    this.merge(aggregate);
    return aggregate;
  }

  @Override
  public void detach(T aggregate) {
    this.getContext().remove(aggregate.getId());
  }

  @Override
  public void merge(T aggregate) {
    // 生成一份追踪快照并保存
    this.getContext().put(this.snapshoot(aggregate));
  }

  @Override
  public Diff detectChanges(T aggregate) {
    // 生成快照进行对比，因为属性可能被Diff持有，有被修改的风险。
    return DiffUtil.diff(this.obtain(aggregate.getId()), aggregate);
  }

  @Override
  public T obtain(ID id) {
    // 使用snapshoot方法将追踪复制一份返回
    return this.getContext().contains(id) ? this.snapshoot(this.getContext().get(id)) : null;
  }
}
