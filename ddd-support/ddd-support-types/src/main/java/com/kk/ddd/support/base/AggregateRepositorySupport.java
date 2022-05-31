package com.kk.ddd.support.base;

import com.kk.ddd.support.marker.AggregateRepository;
import com.kk.ddd.support.marker.Identifier;
import com.kk.ddd.support.tracking.AggregateTrackingManager;
import com.kk.ddd.support.tracking.diff.Diff;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * AggregateRepository支持类 <br>
 * 通过AggregateTrackingManager实现了追踪更新的功能 <br>
 * 缓存和快照需要同时存在，快照只能自己修改，缓存可以被共同修改。
 *
 * @author KaiKoo
 */
@Slf4j
public abstract class AggregateRepositorySupport<T extends Aggregate<ID>, ID extends Identifier>
    extends EntityRepositorySupport<T, ID> implements AggregateRepository<T, ID> {

  @Getter private final AggregateTrackingManager<T, ID> aggregateTrackingManager;

  public AggregateRepositorySupport(
      final AggregateTrackingManager<T, ID> aggregateTrackingManager) {
    this.aggregateTrackingManager = Objects.requireNonNull(aggregateTrackingManager);
  }

  /** EntityRepository 的保存方法实现重写 */
  @Override
  protected void insert0(@NotNull T aggregate) {
    super.insert0(aggregate);
    // 添加跟踪
    this.getAggregateTrackingManager().attach(aggregate);
  }

  @Override
  protected void update0(@NotNull T aggregate) {
    // 完全重写父类更新方法
    // 变更对比
    var diff = this.getAggregateTrackingManager().detectChanges(aggregate);
    // 无变更直接返回
    if (diff.isNoneDiff()) {
      log.info("None diff, return!");
      return;
    }
    super.tryLockThenConsume(aggregate, (t) -> this.onUpdate(t, diff));
    // 合并追踪
    this.getAggregateTrackingManager().merge(aggregate);
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
    this.getAggregateTrackingManager().detach(aggregate);
  }

  /** EntityRepository 的查询方法实现 */
  @Override
  public Optional<T> find(@NotNull ID id) {
    // 先返回追踪，不存在追踪则查询，查询完毕添加追踪。
    return Optional.ofNullable(this.getAggregateTrackingManager().obtain(id))
        .or(() -> super.find(id).map(this.getAggregateTrackingManager()::attach));
  }

  @Override
  public List<T> find(@NotEmpty Set<ID> ids) {
    var list = new ArrayList<T>(ids.size());
    var ids2Lookup =
        ids.stream()
            .filter(
                id -> {
                  var obtained = this.getAggregateTrackingManager().obtain(id);
                  if (null != obtained) {
                    list.add(obtained);
                    return false;
                  }
                  return true;
                })
            .collect(Collectors.toSet());
    if (ids2Lookup.isEmpty()) {
      return list;
    }
    super.find(ids2Lookup).forEach(t -> list.add(this.getAggregateTrackingManager().attach(t)));
    return list;
  }
}
