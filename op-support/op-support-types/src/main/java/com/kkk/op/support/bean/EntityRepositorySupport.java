package com.kkk.op.support.bean;

import com.kkk.op.support.annotations.Cacheable;
import com.kkk.op.support.exception.BussinessException;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.DistributedReentrantLock;
import com.kkk.op.support.marker.Entity;
import com.kkk.op.support.marker.EntityRepository;
import com.kkk.op.support.marker.Identifier;
import java.util.List;
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
    private DistributedReentrantLock distributedReentrantLock;

    @Getter(AccessLevel.PROTECTED)
    private CacheManager<T> cacheManager;

    @Getter(AccessLevel.PROTECTED)
    private final boolean autoCaching;

    {
        Cacheable annotation = this.getClass().getAnnotation(Cacheable.class);
        autoCaching = annotation == null ? false : annotation.autoCaching();
    }

    public EntityRepositorySupport(
            DistributedReentrantLock distributedReentrantLock,
            CacheManager<T> cacheManager) {
        this.distributedReentrantLock = distributedReentrantLock;
        this.cacheManager = cacheManager;
    }

    /**
     * 这几个方法是继承的子类应该去实现的 对应crud的实现
     */

    protected abstract T onSelect(@NotNull ID id);

    protected abstract void onDelete(@NotNull T entity);

    protected abstract void onInsertOrUpdate(@NotNull T entity);

    protected abstract List<T> onSelectByIds(@NotEmpty Set<ID> ids);

    @Override
    public T find(@NotNull ID id) {
        if (!this.autoCaching) {
            return this.onSelect(id);
        }
        var key = id.getValue();
        var entity = this.cacheManager.cacheGet(key);
        if (entity != null) {
            return entity;
        }
        this.distributedReentrantLock.tryLock(key);
        try {
            entity = this.onSelect(id);
            this.cacheManager.cachePut(key, entity);
            return entity;
        } catch (Exception e) {
            throw new BussinessException(e);
        } finally {
            this.distributedReentrantLock.unlock(key);
        }
    }

    @Override
    public void remove(@NotNull T entity) {
        if (!this.autoCaching) {
            this.onDelete(entity);
            return;
        }
        var key = entity.getId().getValue();
        this.distributedReentrantLock.tryLock(key);
        try {
            this.cacheManager.cacheRemove(key);
            this.onDelete(entity);
            // todo... 发送消息，延迟双删
            return;
        } catch (Exception e) {
            throw new BussinessException(e);
        } finally {
            this.distributedReentrantLock.unlock(key);
        }
    }

    @Override
    public void save(@NotNull T entity) {
        if (!this.autoCaching) {
            this.onDelete(entity);
            return;
        }
        var key = entity.getId().getValue();
        this.distributedReentrantLock.tryLock(key);
        try {
            this.cacheManager.cacheRemove(key);
            this.onInsertOrUpdate(entity);
            // todo... 发送消息，延迟双删
            return;
        } catch (Exception e) {
            throw new BussinessException(e);
        } finally {
            this.distributedReentrantLock.unlock(key);
        }
    }

    @Override
    public List<T> list(@NotEmpty Set<ID> ids) {
        //todo... 批量加锁 or 单个加锁 or 异步加载缓存
        return this.onSelectByIds(ids);
    }

}
