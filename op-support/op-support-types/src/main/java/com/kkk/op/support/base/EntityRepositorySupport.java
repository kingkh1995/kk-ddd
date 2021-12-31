package com.kkk.op.support.base;

import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.marker.EntityRepository;
import com.kkk.op.support.marker.Identifier;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存实现 <br>
 * 缓存数据库一致性方案： <br>
 * 在写请求时，先淘汰缓存之前，获取该分布式锁。 <br>
 * 在读请求时，发现缓存不存在时，可以先获取分布式锁，防止缓存击穿。 <br>
 * 如果存在主从复制延迟，使用延迟双删，延迟时间为主从复制延迟时间， <br>
 * 仍需要使用 select for update 或乐观锁等手段防止并非问题，因为锁可能自动过期
 *
 * @author KaiKoo
 */
@Slf4j
public abstract class EntityRepositorySupport<T extends Entity<ID>, ID extends Identifier>
    implements EntityRepository<T, ID> {

  // ===============================================================================================

  /**
   * 以下方法是继承的子类应该去实现的 （模板方法设计模式）<br>
   * 对应crud的实现
   */
  protected abstract void onInsert(@NotNull T entity);

  protected abstract void onUpdate(@NotNull T entity);

  protected abstract void onDelete(@NotNull T entity);

  protected abstract Optional<T> onSelect(@NotNull ID id);

  protected abstract List<T> onSelectByIds(@NotEmpty Set<ID> ids);

  // ===============================================================================================

  // 定义一个tryRun方法，使用函数式接口，使实现可以随意替换
  protected void tryLockThenConsume(@NotNull T entity, @NotNull Consumer<? super T> consumer) {
    if (!EntityLocker.tryLockThenRun(entity, () -> consumer.accept(entity))) {
      throw new BusinessException("尝试的人太多了，请稍后再试！");
    }
  }

  @Override
  public void save(@NotNull T entity) {
    if (entity.isIdentified()) {
      this.update0(entity);
    } else {
      this.insert0(entity);
    }
  }

  protected void insert0(@NotNull T entity) {
    // insert操作不需要获取分布式锁
    this.onInsert(entity);
  }

  protected void update0(@NotNull T entity) {
    // update操作需要获取分布式锁
    this.tryLockThenConsume(entity, this::onUpdate);
  }

  @Override
  public void remove(@NotNull T entity) {
    // delete操作需要获取分布式锁
    this.tryLockThenConsume(entity, this::onDelete);
  }

  @Override
  public Optional<T> find(@NotNull ID id) {
    return this.onSelect(id);
  }

  @Override
  public List<T> find(@NotEmpty Set<ID> ids) {
    return this.onSelectByIds(ids);
  }
}
