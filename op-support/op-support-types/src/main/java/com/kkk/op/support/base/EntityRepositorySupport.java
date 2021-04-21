package com.kkk.op.support.base;

import com.kkk.op.support.annotations.Cacheable;
import com.kkk.op.support.exception.BussinessException;
import com.kkk.op.support.marker.Cache;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.EntityRepository;
import com.kkk.op.support.marker.Identifier;
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
    private final boolean autoCaching;

    {
        var annotation = this.getClass().getAnnotation(Cacheable.class);
        this.autoCaching = annotation == null ? false : annotation.autoCaching();
    }

    public EntityRepositorySupport(
            DistributedLock distributedLock,
            Cache<T> cache) {
        // 开启自动缓存时才校验
        if (this.isAutoCaching()) {
            Objects.requireNonNull(distributedLock);
            Objects.requireNonNull(cache);
        }
        this.distributedLock = distributedLock;
        this.cache = cache;
    }

    protected abstract String generateCacheKey(@NotNull ID id);

    /**
     * 这几个方法是继承的子类应该去实现的 对应crud的实现
     */
    protected abstract T onSelect(@NotNull ID id);

    protected abstract void onDelete(@NotNull T entity);

    protected abstract void onInsert(@NotNull T entity);

    protected abstract void onUpdate(@NotNull T entity);

    protected abstract List<T> onSelectByIds(@NotEmpty Set<ID> ids);

    @Override
    public T find(@NotNull ID id) {
        if (!this.autoCaching) {
            return this.onSelect(id);
        }
        var key = this.generateCacheKey(id);
        var entity = this.cache.get(key);
        if (entity != null) {
            return entity;
        }
        if (this.distributedLock.tryLock(key)) {
            try {
                entity = this.onSelect(id);
                this.cache.put(key, entity);
                return entity;
            } finally {
                this.distributedLock.unlock(key);
            }
        } else {
            throw new BussinessException("服务繁忙请稍后再试！");
        }
    }

    @Override
    public void remove(@NotNull T entity) {
        if (!this.autoCaching) {
            this.onDelete(entity);
            return;
        }
        var key = this.generateCacheKey(entity.getId());
        if (this.distributedLock.tryLock(key)) {
            try {
                this.cache.remove(key);
                this.onDelete(entity);
                // todo... 发送消息，延迟双删
                return;
            } finally {
                this.distributedLock.unlock(key);
            }
        } else {
            throw new BussinessException("服务繁忙请稍后再试！");
        }
    }

    @Override
    public void save(@NotNull T entity) {
        // insert操作不需要获取分布式锁
        if (entity.getId() == null) {
            this.onInsert(entity);
            return;
        }
        // update操作
        if (!this.autoCaching) {
            this.onUpdate(entity);
            return;
        }
        var key = this.generateCacheKey(entity.getId());
        if (this.distributedLock.tryLock(key)) {
            try {
                this.cache.remove(key);
                this.onUpdate(entity);
                // todo... 发送消息，延迟双删
                return;
            } finally {
                this.distributedLock.unlock(key);
            }
        } else {
            throw new BussinessException("服务繁忙请稍后再试！");
        }
    }

    @Override
    public List<T> list(@NotEmpty Set<ID> ids) {
        //todo... 批量加锁 or 单个加锁
        return this.onSelectByIds(ids);
    }

}
