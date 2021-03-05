package com.kkk.op.support.changeTracking;

import com.kkk.op.support.marker.Aggregate;
import com.kkk.op.support.marker.Identifier;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Aggregate快照管理实现类
 * 使用ThreadLocal防止多个线程公用一份快照
 * @author KaiKoo
 */
public class ThreadLocalAggregateSnapshotContext<T extends Aggregate<ID>, ID extends Identifier> implements
        AggregateSnapshotContext<T, ID> {

    private final ThreadLocal<Map<ID, T>> threadLocal = ThreadLocal.withInitial(HashMap::new);

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
        T snapshot = (T) aggregate.snapshot();
        this.threadLocal.get().put(snapshot.getId(), snapshot);
    }

    @Override
    public T getSnapshot(@NotNull ID id) {
        T snapshot = this.threadLocal.get().get(id);
        //返回快照的快照，防止快照被修改
        return (T) snapshot.snapshot();
    }

}
