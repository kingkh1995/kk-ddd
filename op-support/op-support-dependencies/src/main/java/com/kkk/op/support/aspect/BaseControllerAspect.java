package com.kkk.op.support.aspect;

import com.kkk.op.support.bean.Result;
import com.kkk.op.support.bean.Uson;
import com.kkk.op.support.exception.BusinessException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller层日志打印切面 <br>
 * todo... 添加工具类保存请求参数
 *
 * @author KaiKoo
 */
@Slf4j
@Aspect // 切面类需要添加 @Aspect 注解
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置级别最高
@Component // 需要添加 @Component 注解
public class BaseControllerAspect extends AbstractMethodAspect {

  @Autowired private Uson uson;

  @Override
  @Pointcut("@within(com.kkk.op.support.annotations.BaseController)") // 切面针对注解标识的类
  protected void pointcut() {}

  @Override
  public boolean onBefore(ProceedingJoinPoint point) {
    var signature = (MethodSignature) point.getSignature();
    var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    log.info(
        "[({}){}] ~ [{}.{}()] ~ [request = {}]",
        request.getMethod(),
        request.getRequestURI(),
        signature.getDeclaringTypeName(),
        signature.getName(),
        this.uson.toJson(getMethodParams(signature, point.getArgs())));
    return super.onBefore(point);
  }

  private Map<String, Object> getMethodParams(MethodSignature signature, Object[] args) {
    if (args == null || args.length == 0) {
      return Collections.EMPTY_MAP;
    }
    var params = new HashMap<String, Object>();
    var parameterNames = signature.getParameterNames();
    for (int i = 0; i < args.length; i++) {
      var param = args[i];
      if (param instanceof Model
          || param instanceof ModelMap
          || param instanceof HttpServletRequest
          || param instanceof HttpServletResponse
          || param instanceof MultipartFile
          || param instanceof MultipartFile[]) {
        continue;
      }
      params.put(parameterNames[i], param);
    }
    return params;
  }

  @Override
  public Object getOnThrow(ProceedingJoinPoint point, Throwable e) throws Throwable {
    // 单独处理 BusinessException 不打印日志了
    if (e instanceof BusinessException) {
      return Result.fail(e.getMessage());
    }
    // 其他情况抛出异常，交由全局异常处理
    return super.getOnThrow(point, e);
  }

  @Override
  public void onComplete(
      ProceedingJoinPoint point, boolean permitted, boolean thrown, @Nullable Object result) {
    MethodSignature signature = (MethodSignature) point.getSignature();
    if (result != null) {
      ((Result<?>) result).addExtend("requestTime", Instant.now().toEpochMilli());
    }
    log.info(
        "[{}.{}()] ~ [permitted:{}] ~ [thrown:{}] ~ [response = {}]",
        signature.getDeclaringTypeName(),
        signature.getName(),
        permitted,
        thrown,
        this.uson.toJson(result));
  }
}
