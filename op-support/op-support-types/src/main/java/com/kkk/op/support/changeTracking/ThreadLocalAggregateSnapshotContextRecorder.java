package com.kkk.op.support.changeTracking;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.Identifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ThreadLocalAggregateSnapshotContext记录器
 *
 * @author KaiKoo
 */
public final class ThreadLocalAggregateSnapshotContextRecorder {

    private ThreadLocalAggregateSnapshotContextRecorder() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    private final static ThreadLocal<Set<ThreadLocal>> RECORDER = ThreadLocal
            .withInitial(HashSet::new);

    // 定义为泛型方法
    public static <ID extends Identifier, T extends Aggregate<ID>> void record(
            ThreadLocal<Map<ID, T>> threadLocal) {
        var threadLocalSet = RECORDER.get();
        threadLocalSet.add(threadLocal);
    }

    public static void remove() {
        var threadLocalSet = RECORDER.get();
        threadLocalSet.forEach(ThreadLocal::remove);
        RECORDER.remove();
    }

}
