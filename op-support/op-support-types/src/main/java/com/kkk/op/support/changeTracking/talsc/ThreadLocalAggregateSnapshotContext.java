package com.kkk.op.support.changeTracking.talsc;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.changeTracking.AggregateSnapshotContext;
import com.kkk.op.support.marker.Identifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.context.ApplicationContext;

/**
 * Aggregate快照管理实现类
 * 使用ThreadLocal防止多个线程公用一份快照
 * 内存泄漏怎么解决？ todo...待优化
 * 1.发布一个AggregateTrackedEvent事件
 * 2.将添加了快照的threadLocal加入ThreadLocalAggregateSnapshotContextHolder
 * 3.在拦截器的afterCompletion方法中移除所有的threadLocal）
 *
 * @author KaiKoo
 */
public class ThreadLocalAggregateSnapshotContext<T extends Aggregate<ID>, ID extends Identifier> implements
        AggregateSnapshotContext<T, ID>{

    private final ThreadLocal<Map<ID, T>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    private ApplicationContext applicationContext;

    public ThreadLocalAggregateSnapshotContext(
            ApplicationContext applicationContext) {
        this.applicationContext = Objects.requireNonNull(applicationContext);
    }

    @Override
    public boolean existSnapshot(@NotNull ID id) {
        return this.threadLocal.get().containsKey(id);
    }

    @Override
    public T removeSnapshot(@NotNull ID id) {
        return this.threadLocal.get().remove(id);
    }

    @Override
    public void putSnapshot(@NotNull T aggregate) {
        //获取快照
        if (aggregate.getId() != null) {
            var snapshot = (T) aggregate.snapshot();
            this.threadLocal.get().put(snapshot.getId(), snapshot);
            // 发布事件，在拦截器中移除所有ThreadLocal
            applicationContext.publishEvent(new TlascTrackedEvent<>(this, threadLocal));
        }
    }

    @Override
    public T getSnapshot(@NotNull ID id) {
        var snapshot = this.threadLocal.get().get(id);
        //返回快照的快照，防止快照被修改
        return (T) snapshot.snapshot();
    }

}
