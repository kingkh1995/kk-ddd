package com.kkk.op.support.base;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.Optional;
import org.springframework.lang.Nullable;

/**
 * 参考RequestContextHolder设计 <br>
 *
 * @author KaiKoo
 */
public final class LocalRequestContextHolder {

  private LocalRequestContextHolder() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final ThreadLocal<LocalRequestContext> contextHolder = new ThreadLocal<>();

  /**
   * ThreadLocal增强：使用TransmittableThreadLocal <br>
   * 线程池需要使用TtlExecutors类对应的静态方法包装
   */
  private static final TransmittableThreadLocal<LocalRequestContext> inheritableContextHolder =
      new TransmittableThreadLocal<>();

  public static void reset() {
    contextHolder.remove();
    inheritableContextHolder.remove();
  }

  // 默认开启TransmittableThreadLocal
  public static void set(@Nullable LocalRequestContext requestContext) {
    set(requestContext, true);
  }

  public static void set(@Nullable LocalRequestContext requestContext, boolean inheritable) {
    if (requestContext == null) {
      reset();
    } else {
      if (inheritable) {
        inheritableContextHolder.set(requestContext);
        contextHolder.remove();
      } else {
        contextHolder.set(requestContext);
        inheritableContextHolder.remove();
      }
    }
  }

  @Nullable
  public static LocalRequestContext get() {
    return Optional.ofNullable(contextHolder.get()).orElse(inheritableContextHolder.get());
  }
}
