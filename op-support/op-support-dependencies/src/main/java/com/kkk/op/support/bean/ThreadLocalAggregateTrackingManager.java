package com.kkk.op.support.bean;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.changeTracking.AbstractAggregateTrackingManager;
import com.kkk.op.support.changeTracking.AggregateSnapshotContext;
import com.kkk.op.support.marker.Identifier;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * 基于ThreadLocal的追踪更新Manager实现类
 *
 * @author KaiKoo
 */
public class ThreadLocalAggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier> extends
        AbstractAggregateTrackingManager<T, ID> {

    public ThreadLocalAggregateTrackingManager() {
        super(new ThreadLocalAggregateSnapshotContext());
    }

    /**
     * Aggregate快照管理实现类
     * 使用ThreadLocal防止多个线程公用一份快照
     * 内存泄漏解决方案：
     * 1.putSnapshot 方法将 ThreadLocal 记录到 Recorder
     * 2.在拦截器的 afterCompletion 方法中移除所有的 ThreadLocal
     *
     * @author KaiKoo
     */
    public static class ThreadLocalAggregateSnapshotContext<T extends Aggregate<ID>, ID extends Identifier> implements
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
            if (aggregate.getId() != null) {
                var snapshot = (T) aggregate.snapshot();
                this.threadLocal.get().put(snapshot.getId(), snapshot);
                // 记录到 Recorder
                ThreadLocalRecorder.recordTlasc(this.threadLocal);
            }
        }

        @Override
        public T getSnapshot(@NotNull ID id) {
            var snapshot = this.threadLocal.get().get(id);
            //返回快照的快照，防止快照被修改
            return (T) snapshot.snapshot();
        }

    }

}
