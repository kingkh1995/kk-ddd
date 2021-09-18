package com.kkk.op.support.handler;

import com.kkk.op.support.bean.LocalRequestContext;
import com.kkk.op.support.bean.LocalRequestContextHolder;
import java.time.ZoneId;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * BaseRequestContextHolder处理拦截器 <br>
 * todo... 待完善
 *
 * @author KaiKoo
 */
@Slf4j
public class LocalRequestInterceptor implements HandlerInterceptor {

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
    var requestContext =
        LocalRequestContext.builder()
            .entrance("(" + request.getMethod() + ")" + request.getRequestURI())
            .zoneId(
                Optional.ofNullable(request.getHeader("Zone-Id"))
                    .map(ZoneId::of)
                    .orElse(ZoneId.systemDefault()))
            .build();
    // 打印请求参数
    log.info("{}", requestContext);
    LocalRequestContextHolder.setLocalRequestContext(requestContext);
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    // 完成时清空http请求信息
    LocalRequestContextHolder.resetLocalRequestContext();
  }
}
