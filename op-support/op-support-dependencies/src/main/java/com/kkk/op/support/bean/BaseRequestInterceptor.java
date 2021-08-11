package com.kkk.op.support.bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * BaseRequestContextHolder处理拦截器 <br>
 * todo... 待实现
 *
 * @author KaiKoo
 */
@Slf4j
public class BaseRequestInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    var requestContext =
        BaseRequestContext.builder()
            .entrance("(" + request.getMethod() + ")" + request.getRequestURI())
            .build();
    // 打印请求参数
    log.info("[requestContext = {}]", requestContext);
    BaseRequestContextHolder.setBaseRequestContext(requestContext);
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    BaseRequestContextHolder.resetBaseRequestContext();
  }
}
