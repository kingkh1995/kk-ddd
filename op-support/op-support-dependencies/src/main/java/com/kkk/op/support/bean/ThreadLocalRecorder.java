package com.kkk.op.support.bean;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.Identifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ThreadLocal记录器 <br>
 * 用于在拦截器中移除ThreadLock以免造成内存泄漏 <br>
 * todo... dubbo拦截器移除
 *
 * @author KaiKoo
 */
public final class ThreadLocalRecorder {

  private ThreadLocalRecorder() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final ThreadLocal<Set<ThreadLocal<?>>> RECORDER =
      ThreadLocal.withInitial(HashSet::new);

  public static void record(ThreadLocal<?> threadLocal) {
    RECORDER.get().add(threadLocal);
  }

  /** 记录Talsc使用 定义为泛型方法 */
  public static <ID extends Identifier, T extends Aggregate<ID>> void recordTlasc(
      ThreadLocal<Map<ID, T>> threadLocal) {
    RECORDER.get().add(threadLocal);
  }

  public static void remove() {
    RECORDER.get().forEach(ThreadLocal::remove);
    RECORDER.remove();
  }
}
