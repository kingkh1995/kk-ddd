package com.kkk.op.support.base;

import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.CacheableRepository;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.EntityRepository;
import com.kkk.op.support.marker.Identifier;
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
  private final DistributedLock distributedLock;

  @Getter(AccessLevel.PROTECTED)
  private final String lockNamePrefix;

  @Nullable private final CacheManager cacheManager;

  @Getter(AccessLevel.PROTECTED)
  private final boolean autocaching;

  @Getter(AccessLevel.PROTECTED)
  private final String cacheKeyPrefix;

  {
    // 设置autocaching
    this.autocaching = this.getClass().getAnnotation(AutoCaching.class) != null;
    // 该方法的主体是具体的业务子类，所以获取到的泛型父类是：EntityRepositorySupport<具体的Entity, 具体的Identifier>为参数化类型
    var type = (ParameterizedType) this.getClass().getGenericSuperclass();
    // 设置tClass 参数化类型获取实际Type
    this.tClass = (Class<T>) type.getActualTypeArguments()[0];
    var split = this.tClass.getCanonicalName().split("\\.");
    var className = split[split.length - 1];
    // 设置cacheKeyPrefix
    this.cacheKeyPrefix =
        split.length > 4 ? String.format("%s:%s:", split[3].toUpperCase(), className) : className;
    // 设置lockNamePrefix
    this.lockNamePrefix = "LOCK:" + this.cacheKeyPrefix;
  }

  public EntityRepositorySupport(
      DistributedLock distributedLock, @Nullable CacheManager cacheManager) {
    // 开启自动缓存时才需要CacheManager
    if (this.isAutocaching()) {
      Objects.requireNonNull(cacheManager);
    }
    this.cacheManager = cacheManager;
    this.distributedLock = Objects.requireNonNull(distributedLock);
  }

  // todo... 如何更优雅的实现
  public String generateLockName(@NotNull ID id) {
    return this.getLockNamePrefix() + id.identifier();
  }

  // ===============================================================================================

  /**
   * 以下方法是继承的子类应该去实现的 （模板方法设计模式）<br>
   * 对应crud的实现
   */
  protected abstract Optional<T> onSelect(@NotNull ID id);
  // todo... 由子类自行实现查询操作防止缓存击穿 三种方式：1、分布式锁 2、顺序队列 3、信号量

  protected abstract void onDelete(@NotNull T entity);

  protected abstract void onInsert(@NotNull T entity);

  protected abstract void onUpdate(@NotNull T entity);

  protected abstract List<T> onSelectByIds(@NotEmpty Set<ID> ids);

  // ===============================================================================================

  /** 以下是Cache相关方法 */
  protected CacheManager getCacheManager() {
    return Objects.requireNonNull(this.cacheManager);
  }

  @Override
  public void cachePut(@NotNull T t) {
    this.getCacheManager().put(this.generateCacheKey(t.getId()), t);
  }

  @Override
  public Optional<T> cacheGet(@NotNull ID id) {
    return this.getCacheManager().get(this.generateCacheKey(id), this.getTClass());
  }

  @Override
  public boolean cacheRemove(@NotNull T t) {
    return this.getCacheManager().remove(this.generateCacheKey(t.getId()));
  }

  public String generateCacheKey(@NotNull ID id) {
    return this.getCacheKeyPrefix() + id.identifier();
  }

  // ===============================================================================================

  @Override
  public Optional<T> find(@NotNull ID id) {
    if (!this.isAutocaching()) {
      return this.onSelect(id);
    }
    return this.cacheGet(id)
        .or(
            () -> {
              var op = this.onSelect(id);
              op.ifPresent(this::cachePut);
              return op;
            });
  }

  @Override
  public void remove(@NotNull T entity) {
    var finished =
        this.getDistributedLock()
            .tryRun(
                this.generateLockName(entity.getId()),
                () -> {
                  if (this.isAutocaching()) {
                    this.cacheDoubleRemove(entity, () -> this.onDelete(entity));
                  } else {
                    this.onDelete(entity);
                  }
                });
    if (!finished) {
      throw new BusinessException("尝试的人太多了，请稍后再试！");
    }
  }

  @Override
  public void save(@NotNull T entity) {
    // insert操作不需要获取分布式锁
    if (!entity.isIdentified()) {
      this.onInsert(entity);
      return;
    }
    this.update0(entity, this::onUpdate);
  }

  // 定义一个update0方法，使用函数式接口，使得update实现可以随意替换
  protected void update0(@NotNull T entity, Consumer<T> consumer) {
    // update操作
    var finished =
        this.getDistributedLock()
            .tryRun(
                this.generateLockName(entity.getId()),
                () -> {
                  if (this.isAutocaching()) {
                    this.cacheDoubleRemove(entity, () -> consumer.accept(entity));
                  } else {
                    consumer.accept(entity);
                  }
                });
    if (!finished) {
      throw new BusinessException("尝试的人太多了，请稍后再试！");
    }
  }

  @Override
  public List<T> list(@NotEmpty Set<ID> ids) {
    if (!this.isAutocaching()) {
      return this.onSelectByIds(ids);
    }
    // 有缓存情况下
    // ArrayList初始容量指定为ids的大小
    var list = new ArrayList<T>(ids.size());

    ids =
        ids.stream()
            .filter(
                (id) -> {
                  // 先查缓存 存在则直接取缓存 否则收集
                  var optional = this.cacheGet(id);
                  if (optional.isEmpty()) {
                    return true;
                  } else {
                    list.add(optional.get());
                    return false;
                  }
                })
            .collect(Collectors.toSet());
    // 不存在缓存的去查数据库，并加载缓存
    if (!ids.isEmpty()) {
      this.onSelectByIds(ids)
          .forEach(
              (t) -> {
                list.add(t);
                this.cachePut(t);
              });
    }
    return list;
  }
}
