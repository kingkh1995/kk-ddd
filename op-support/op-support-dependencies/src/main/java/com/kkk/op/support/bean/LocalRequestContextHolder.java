package com.kkk.op.support.bean;

import com.alibaba.ttl.TransmittableThreadLocal;
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
   * 线程池需要使用TtlExecutors.getTtlExecutorService()包装
   */
  private static final TransmittableThreadLocal<LocalRequestContext> inheritableContextHolder =
      new TransmittableThreadLocal<>();

  public static void resetLocalRequestContext() {
    contextHolder.remove();
    inheritableContextHolder.remove();
  }

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
    var requestContext = contextHolder.get();
    if (requestContext == null) {
      requestContext = inheritableContextHolder.get();
    }
    return requestContext;
  }
}
