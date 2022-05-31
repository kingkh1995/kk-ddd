package com.kk.ddd.support.bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ThreadLocal移除拦截器 <br>
 * 线程池会复用ThreadLocal，需要在一次请求完成之后执行remove操作
 *
 * @author KaiKoo
 */
public class ThreadLocalRemoveInterceptor implements HandlerInterceptor {

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    ThreadLocalRecorder.removeAll();
    // InternalThreadLocal.removeAll();
  }
}
