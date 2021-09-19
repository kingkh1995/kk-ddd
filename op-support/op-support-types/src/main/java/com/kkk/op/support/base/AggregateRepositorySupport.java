package com.kkk.op.support.base;

import com.kkk.op.support.changeTracking.AggregateTrackingManager;
import com.kkk.op.support.changeTracking.diff.Diff;
import com.kkk.op.support.marker.AggregateRepository;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.Identifier;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.lang.Nullable;

/**
 * AggregateRepository支持类 <br>
 * 通过AggregateTrackingManager实现了追踪更新的功能 <br>
 * 缓存和快照需要同时存在，快照只能自己修改，缓存可以被共同修改。
 *
 * @author KaiKoo
 */
public abstract class AggregateRepositorySupport<T extends Aggregate<ID>, ID extends Identifier>
    extends EntityRepositorySupport<T, ID> implements AggregateRepository<T, ID> {

  @Getter(AccessLevel.PROTECTED)
  private final AggregateTrackingManager<T, ID> aggregateTrackingManager;

  public AggregateRepositorySupport(
      @NotNull DistributedLock distributedLock,
      @Nullable CacheManager cacheManager,
      @NotNull AggregateTrackingManager<T, ID> aggregateTrackingManager) {
    super(distributedLock, cacheManager);
    this.aggregateTrackingManager = Objects.requireNonNull(aggregateTrackingManager);
  }

  /**
   * 让查询出来的对象能够被追踪。 <br>
   * 如果自己实现了一个定制查询接口，要记得单独调用 attach。
   */
  @Override
  public void attach(@NotNull T aggregate) {
    this.getAggregateTrackingManager().attach(aggregate);
  }

  /**
   * 停止追踪。 <br>
   * 如果自己实现了一个定制移除接口，要记得单独调用 detach。
   */
  @Override
  public void detach(@NotNull T aggregate) {
    this.getAggregateTrackingManager().detach(aggregate);
  }

  /** EntityRepository 的保存方法实现 */
  @Override
  public void save(@NotNull T aggregate) {
    // insert操作
    if (!aggregate.isIdentified()) {
      this.onInsert(aggregate);
      // 添加跟踪
      this.attach(aggregate);
      return;
    }
    // update操作
    // 变更对比
    var diff = this.getAggregateTrackingManager().detectChanges(aggregate);
    // 无变更直接返回
    if (diff.isNoneDiff()) {
      // TBD 特殊提示 httpstatus
      return;
    }
    super.tryRun(aggregate, (t) -> this.onUpdate(t, diff));
    // 合并跟踪变更
    this.getAggregateTrackingManager().merge(aggregate);
    return;
  }

  /**
   * 重新定义update的实现，原update方法设置为不支持的操作。 <br>
   * 调用方注意，调用update操作前一定要查询一下加载快照。
   */
  protected abstract void onUpdate(@NotNull T aggregate, @NotNull Diff diff);

  @Override
  protected final void onUpdate(@NotNull T aggregate) {
    throw new UnsupportedOperationException();
  }

  /** EntityRepository 的移除方法实现 */
  @Override
  public void remove(@NotNull T aggregate) {
    super.remove(aggregate);
    // 解除跟踪
    this.detach(aggregate);
  }

  /** EntityRepository 的查询方法实现 */
  @Override
  public Optional<T> find(@NotNull ID id) {
    var op = super.find(id);
    // 添加跟踪
    op.ifPresent(this::attach);
    return op;
  }

  @Override
  public List<T> list(@NotEmpty Set<ID> ids) {
    var list = super.list(ids);
    list.forEach(this::attach);
    return list;
  }
}
