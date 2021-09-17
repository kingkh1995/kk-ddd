package com.kkk.op.support.bean;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.Identifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * ThreadLocal记录器 <br>
 * 用于在拦截器中移除ThreadLock以免造成内存泄漏 <br>
 * todo... dubbo移除拦截器
 *
 * @see ThreadLocalRemoveInterceptor spring移除拦截器
 * @author KaiKoo
 */
public final class ThreadLocalRecorder {

  private ThreadLocalRecorder() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  // 使用IdentityHashMap（直接使用==对比）再包装为Set
  private static final ThreadLocal<Set<ThreadLocal<?>>> recorder =
      ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

  public static void record(ThreadLocal<?> threadLocal) {
    recorder.get().add(threadLocal);
  }

  /** 记录Talsc使用 定义为泛型方法 */
  public static <ID extends Identifier, T extends Aggregate<ID>> void recordTlasc(
      ThreadLocal<Map<ID, T>> threadLocal) {
    recorder.get().add(threadLocal);
  }

  public static void removeAll() {
    var it = recorder.get().iterator();
    while (it.hasNext()) {
      it.next().remove();
      it.remove();
    }
  }
}
