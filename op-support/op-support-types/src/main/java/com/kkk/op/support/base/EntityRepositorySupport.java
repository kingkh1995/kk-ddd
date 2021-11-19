package com.kkk.op.support.base;

import com.kkk.op.support.annotation.AutoCaching;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.marker.CacheableRepository;
import com.kkk.op.support.marker.DistributedLocker;
import com.kkk.op.support.marker.EntityCache;
import com.kkk.op.support.marker.EntityCache.ValueWrapper;
import com.kkk.op.support.marker.EntityRepository;
import com.kkk.op.support.marker.Identifier;
import com.kkk.op.support.marker.NameGenerator;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

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
    implements EntityRepository<T, ID>, CacheableRepository<T, ID> {

  @Getter(AccessLevel.PROTECTED)
  private final Class<T> tClass;

  @Getter(AccessLevel.PROTECTED)
  private final DistributedLocker distributedLocker;

  @Getter(AccessLevel.PROTECTED)
  private final String[] cnSplit;

  @Nullable private final EntityCache cache;

  @Getter private final boolean autoCaching;

  {
    // 设置autoCaching
    this.autoCaching = this.getClass().isAnnotationPresent(AutoCaching.class);
    // fixme... 这种写法不安全
    // 该方法的主体是具体的业务子类，所以获取到的泛型父类是：EntityRepositorySupport<具体的Entity, 具体的Identifier>为参数化类型
    var type = (ParameterizedType) this.getClass().getGenericSuperclass();
    // 设置tClass 参数化类型获取实际Type
    this.tClass = (Class<T>) type.getActualTypeArguments()[0];
    this.cnSplit = this.tClass.getCanonicalName().split("\\.");
  }

  public EntityRepositorySupport(
      @NotNull DistributedLocker distributedLocker, @Nullable EntityCache cache) {
    this.distributedLocker = Objects.requireNonNull(distributedLocker);
    // 开启自动缓存时才需要CacheManager
    if (this.isAutoCaching()) {
      Objects.requireNonNull(cache);
    }
    this.cache = cache;
  }

  // ===============================================================================================

  /**
   * 以下方法是继承的子类应该去实现的 （模板方法设计模式）<br>
   * 对应crud的实现
   */
  protected abstract void onInsert(@NotNull T entity);

  protected abstract void onUpdate(@NotNull T entity);

  protected abstract void onDelete(@NotNull T entity);

  // todo... 由子类自行实现查询操作防止缓存击穿 三种方式：1、分布式锁 2、顺序队列 3、信号量
  protected abstract Optional<T> onSelect(@NotNull ID id);

  protected abstract List<T> onSelectByIds(@NotEmpty Set<ID> ids);

  // ===============================================================================================

  /** 以下是Cache相关方法 */
  @Override
  public EntityCache getCache() {
    return Objects.requireNonNull(this.cache);
  }

  @Override
  public String generateCacheKey(ID id) {
    return NameGenerator.joiner(":", "", "")
        .generate(
            cnSplit[2],
            cnSplit[3],
            cnSplit[cnSplit.length - 1],
            Objects.requireNonNull(id).identifier());
  }

  @Override
  public Optional<ValueWrapper<T>> cacheGetIfPresent(ID id) {
    return this.getCache().get(this.generateCacheKey(id), this.getTClass());
  }

  @Override
  public Optional<T> cacheGet(ID id) {
    // 加载时会获取锁，防止了缓存穿透，但是会有线程阻塞，如果需要fail-fast要自行实现。
    return this.getCache().get(this.generateCacheKey(id), () -> this.onSelect(id).orElse(null));
  }

  @Override
  public void cachePut(T t) {
    this.getCache().put(this.generateCacheKey(Objects.requireNonNull(t).getId()), t);
  }

  @Override
  public boolean cachePutIfAbsent(T t) {
    return this.getCache().putIfAbsent(this.generateCacheKey(Objects.requireNonNull(t).getId()), t);
  }

  @Override
  public void cacheRemove(ID id) {
    this.getCache().evict(this.generateCacheKey(id));
  }

  // ===============================================================================================

  public String generateLockName(@NotNull ID id) {
    return this.distributedLocker
        .getLockNameGenerator()
        .generate(cnSplit[3], cnSplit[cnSplit.length - 1], Objects.requireNonNull(id).identifier());
  }

  // 定义一个tryRun方法，使用函数式接口，使实现可以随意替换
  protected void tryLockThenConsume(@NotNull T entity, @NotNull Consumer<? super T> consumer) {
    var finished =
        this.getDistributedLocker()
            .tryRun(this.generateLockName(entity.getId()), () -> consumer.accept(entity));
    if (!finished) {
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

  protected void update0(@NotNull T entity) {
    // update操作需要获取分布式锁
    this.tryLockThenConsume(
        entity, this.cacheDoubleRemoveWrap(this.isAutoCaching(), this::onUpdate));
  }

  protected void insert0(@NotNull T entity) {
    // insert操作不需要获取分布式锁
    this.cacheDoubleRemoveWrap(this.isAutoCaching(), this::onInsert).accept(entity);
  }

  @Override
  public void remove(@NotNull T entity) {
    // delete操作需要获取分布式锁
    this.tryLockThenConsume(
        entity, this.cacheDoubleRemoveWrap(this.isAutoCaching(), this::onDelete));
  }

  @Override
  public Optional<T> find(@NotNull ID id) {
    if (this.isAutoCaching()) {
      return this.cacheGet(id);
    }
    return this.onSelect(id);
  }

  @Override
  public List<T> list(@NotEmpty Set<ID> ids) {
    if (this.isAutoCaching()) {
      // 有缓存情况，是否有必要批量加载？
      return ids.stream()
          .map(this::cacheGet)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toCollection(() -> new ArrayList<>(ids.size()))); // 指定初始容量避免扩容
    }
    return this.onSelectByIds(ids);
  }
}
