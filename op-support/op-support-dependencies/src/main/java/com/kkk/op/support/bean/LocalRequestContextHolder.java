package com.kkk.op.support.bean;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.kkk.op.support.handler.LocalRequestInterceptor;
import java.util.Optional;
import org.springframework.lang.Nullable;

/**
 * 参考RequestContextHolder设计 <br>
 * todo... 添加dubbo过滤器
 *
 * @see LocalRequestInterceptor spring拦截器 <br>
 * @author KaiKoo
 */
public class LocalRequestContextHolder {

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

  public static void resetLocalRequestContext() {
    contextHolder.remove();
    inheritableContextHolder.remove();
  }

  // 默认开启TransmittableThreadLocal
  public static void setLocalRequestContext(@Nullable LocalRequestContext requestContext) {
    setLocalRequestContext(requestContext, true);
  }

  public static void setLocalRequestContext(
      @Nullable LocalRequestContext requestContext, boolean inheritable) {
    if (requestContext == null) {
      resetLocalRequestContext();
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
  public static LocalRequestContext getLocalRequestContext() {
    return Optional.ofNullable(contextHolder.get()).orElse(inheritableContextHolder.get());
  }
}
