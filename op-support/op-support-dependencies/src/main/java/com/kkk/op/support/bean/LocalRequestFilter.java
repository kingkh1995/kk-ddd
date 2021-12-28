package com.kkk.op.support.bean;

import com.kkk.op.support.base.LocalRequestContext;
import com.kkk.op.support.base.LocalRequestContextHolder;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * LocalRequestContextHolder处理过滤器（servlet过滤器先于spring拦截器执行） <br>
 * todo... 待完善、添加dubbo过滤器
 *
 * @author KaiKoo
 */
@Slf4j
public class LocalRequestFilter extends OncePerRequestFilter {

  private static final String TRACE_ID = "traceId";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 前置保存http请求信息
    var contextBuilder = LocalRequestContext.builder();
    contextBuilder.entrance("(" + request.getMethod() + ")" + request.getRequestURI());
    Optional.ofNullable(request.getHeader("Zone-Id"))
        .map(ZoneId::of)
        .ifPresent(contextBuilder::zoneId);
    Optional.ofNullable(request.getHeader("Source")).ifPresent(contextBuilder::source);
    Optional.ofNullable(request.getHeader("Request-Seq")).ifPresent(contextBuilder::requestSeq);
    var context = contextBuilder.build();
    // 前置添加traceId到logger
    MDC.put(TRACE_ID, context.getTraceId());
    LocalRequestContextHolder.set(context);
    // 执行过滤链
    filterChain.doFilter(request, response);
    // 后置清除
    LocalRequestContextHolder.reset();
    MDC.remove(TRACE_ID);
    log.debug("LocalRequestFilter finish!");
  }
}
