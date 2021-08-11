package com.kkk.op.support.bean;

import org.springframework.lang.Nullable;

/**
 * 参考RequestContextHolder设计 <br>
 * todo... 待实现，添加spring拦截器和dubbo过滤器
 *
 * @author KaiKoo
 */
public class BaseRequestContextHolder {

  private BaseRequestContextHolder() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final ThreadLocal<BaseRequestContext> contextHolder = new ThreadLocal<>();

  /** todo... ThreadLocal增强 */
  private static final ThreadLocal<BaseRequestContext> inheritableContextHolder =
      new ThreadLocal<>();

  public static void resetBaseRequestContext() {
    contextHolder.remove();
    inheritableContextHolder.remove();
  }

  public static void setBaseRequestContext(@Nullable BaseRequestContext requestContext) {
    setBaseRequestContext(requestContext, false);
  }

  public static void setBaseRequestContext(
      @Nullable BaseRequestContext requestContext, boolean inheritable) {
    if (requestContext == null) {
      resetBaseRequestContext();
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
  public static BaseRequestContext getBaseRequestContext() {
    var requestContext = contextHolder.get();
    if (requestContext == null) {
      requestContext = inheritableContextHolder.get();
    }
    return requestContext;
  }
}
