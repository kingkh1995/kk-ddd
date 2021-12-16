package com.kkk.op.support.interceptor;

import com.kkk.op.support.base.LocalRequestContext;
import com.kkk.op.support.base.LocalRequestContextHolder;
import java.time.ZoneId;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * BaseRequestContextHolder处理拦截器 <br>
 * todo... 待完善、添加dubbo过滤器
 *
 * @author KaiKoo
 */
@Slf4j
public class LocalRequestInterceptor implements HandlerInterceptor {

  private static String TRACE_ID = "traceId";

  /**
   * handler参数为HandlerMethod对象 <br>
   * postHandle相当于@afterReturning增强，只有未抛出异常才会调用；afterCompletion相当于@after增强，无论是否执行成功均会调用； <br>
   * 均是在返回结果之前被调用，也无法修改有@ResponseBody注解的控制器的response，方法内调用getWriter也会报错； <br>
   * 因为DispatcherServlet已经于postHandle方法之前将响应提交给HandlerAdapter了。
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 前置保存http请求信息
    var contextBuilder = LocalRequestContext.builder();
    contextBuilder.entrance("(" + request.getMethod() + ")" + request.getRequestURI());
    Optional.ofNullable(request.getHeader("Zone-Id"))
        .map(ZoneId::of)
        .ifPresent(contextBuilder::zoneId);
    Optional.ofNullable(request.getHeader("Source")).ifPresent(contextBuilder::source);
    Optional.ofNullable(request.getHeader("Request-Seq")).ifPresent(contextBuilder::requestSeq);
    var context = contextBuilder.build();
    // 添加traceId到logger
    MDC.put(TRACE_ID, context.getTraceId());
    LocalRequestContextHolder.set(context);
    // 打印请求参数
    log.info("{}", context);
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    // 完成时清空http请求信息
    LocalRequestContextHolder.reset();
    // 移除traceId
    MDC.remove(TRACE_ID);
  }
}
