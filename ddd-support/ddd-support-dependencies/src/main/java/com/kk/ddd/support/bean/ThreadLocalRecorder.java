package com.kk.ddd.support.bean;

import java.util.Collections;
import java.util.IdentityHashMap;
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

  // 使用IdentityHashMap（键和值的比较使用==而不是equals方法）再包装为Set
  private static final ThreadLocal<Set<ThreadLocal<?>>> recorder =
      ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

  public static void record(ThreadLocal<?> threadLocal) {
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
