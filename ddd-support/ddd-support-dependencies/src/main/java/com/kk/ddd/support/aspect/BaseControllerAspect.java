package com.kk.ddd.support.aspect;

import com.kk.ddd.support.bean.Jackson;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller层日志打印切面 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置级别最高
@Aspect // 切面类需要添加 @Aspect 注解
public class BaseControllerAspect extends AbstractMethodAspect {

  @Override
  @Pointcut("@within(com.kk.ddd.support.annotation.BaseController)") // 切面针对注解标识类的所有方法
  protected void pointcut() {}

  @Override
  public boolean onBefore(JoinPoint point) {
    // 前置增强，打印请求信息
    var signature = (MethodSignature) point.getSignature();
    log.info(
        "[{}.{}()] ~ [request = {}]",
        signature.getDeclaringTypeName(),
        signature.getName(),
        Jackson.object2String(getMethodParams(signature, point.getArgs())));
    return true;
  }

  @Override
  public void onSucceed(JoinPoint point, Object result) {
    // 后置增强，成功时打印响应信息
    log.info("[response = {}]", Jackson.object2String(result));
  }

  private Map<String, Object> getMethodParams(MethodSignature signature, Object[] args) {
    if (args == null || args.length == 0) {
      return Collections.emptyMap();
    }
    var parameterNames = signature.getParameterNames();
    var params = new HashMap<String, Object>(args.length, 1.0f);
    for (var i = 0; i < args.length; i++) {
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
    return Collections.unmodifiableMap(params);
  }
}
