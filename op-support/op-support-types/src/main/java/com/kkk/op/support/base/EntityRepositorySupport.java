package com.kkk.op.support.base;

import com.kkk.op.support.annotations.AutoCached;
import com.kkk.op.support.exception.BussinessException;
import com.kkk.op.support.function.Worker;
import com.kkk.op.support.marker.Cache;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.EntityRepository;
import com.kkk.op.support.marker.Identifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存实现
 *
 * 缓存数据库一致性方案：
 * 在写请求时，先淘汰缓存之前，获取该分布式锁。
 * 在读请求时，发现缓存不存在时，先获取分布式锁。
 * 如果存在主从复制延迟，使用延迟双删，延迟时间为主从复制延迟时间，
 * 仍需要使用select for update 或乐观锁等手段防止并非问题，因为锁可能自动过期
 *
 * @author KaiKoo
 */
@Slf4j
public abstract class EntityRepositorySupport<T extends Entity<ID>, ID extends
        Identifier> implements EntityRepository<T, ID> {

    @Getter(AccessLevel.PROTECTED)
    private DistributedLock distributedLock;

    @Getter(AccessLevel.PROTECTED)
    private Cache<T> cache;

    @Getter(AccessLevel.PROTECTED)
    private final boolean autocached;

    @Getter(AccessLevel.PROTECTED)
    private final String cacheKeyPrefix;

    {
        this.autocached = this.getClass().getAnnotation(AutoCached.class) != null;
        // 设置cacheKeyPrefix
        // 获取到泛型父类，即EntityRepositorySupport<具体的Entity, 具体的Identifier> 为参数化类型
        var type = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 因为是具体的类，所有转换为Class
        var split = ((Class) type.getActualTypeArguments()[0]).getCanonicalName().split("\\.");
        var className = split[split.length - 1];
        cacheKeyPrefix =
                split.length > 4 ? String.format("%s:%s:", split[3], className) : className;
    }

    public EntityRepositorySupport(
            DistributedLock distributedLock,
            Cache<T> cache) {
        // 开启自动缓存时才校验
        if (this.isAutocached()) {
            Objects.requireNonNull(cache);
        }
        this.cache = cache;
        this.distributedLock = Objects.requireNonNull(distributedLock);
    }

    protected String generateCacheKey(@NotNull ID id) {
        return cacheKeyPrefix + id.stringValue();
    }

    //==============================================================================================
    // 这几个方法是继承的子类应该去实现的 对应crud的实现
    protected abstract T onSelect(@NotNull ID id); // select操作由子类自行实现防止缓存击穿

    protected abstract void onDelete(@NotNull T entity);

    protected abstract void onInsert(@NotNull T entity);

    protected abstract void onUpdate(@NotNull T entity);

    protected abstract List<T> onSelectByIds(@NotEmpty Set<ID> ids);
    //==============================================================================================

    @Override
    public T find(@NotNull ID id) {
        if (!this.isAutocached()) {
            return this.onSelect(id);
        }
        var key = this.generateCacheKey(id);
        var entity = this.getCache().get(key);
        if (entity != null) {
            return entity;
        }
        entity = this.onSelect(id);
        this.cache.put(key, entity);
        return entity;
    }

    @Override
    public void remove(@NotNull T entity) {
        var key = this.generateCacheKey(entity.getId());
        boolean b = this.getDistributedLock().tryWork(key, () -> {
            if (this.isAutocached()) {
                this.getCache().remove(key);
                this.onDelete(entity);
                // todo... 延迟双删
            } else {
                this.onDelete(entity);
            }
        });
        if (!b) {
            throw new BussinessException("尝试的人太多了，请稍后再试！");
        }
    }

    @Override
    public void save(@NotNull T entity) {
        // insert操作不需要获取分布式锁
        if (entity.getId() == null) {
            this.onInsert(entity);
            return;
        }
        this.update0(entity, () -> this.onUpdate(entity));
    }

    // 定义一个update0方法，使用函数式接口，实现update实现随意替换
    protected void update0(@NotNull T entity, Worker worker) {
        // update操作
        var key = this.generateCacheKey(entity.getId());
        boolean b = this.getDistributedLock().tryWork(key, () -> {
            if (this.isAutocached()) {
                this.getCache().remove(key);
                worker.work();
                // todo... 延迟双删
            } else {
                worker.work();
            }
        });
        if (!b) {
            throw new BussinessException("尝试的人太多了，请稍后再试！");
        }
    }

    @Override
    public List<T> list(@NotEmpty Set<ID> ids) {
        //todo... 批量加锁 or 单个加锁
        return this.onSelectByIds(ids);
    }

}
