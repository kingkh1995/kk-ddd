package com.kk.ddd.support.access;

import com.kk.ddd.support.annotation.AccessCondition;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * AccessCondition切面 <br>
 * 辅助QueryService切面实现访问条件检查，在整个方法内QueryService的调用都会触发校验。
 *
 * @author KaiKoo
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 设置级别低于@BaseControllerAspect切面
@Aspect
public class AccessConditionAspect {

  @Around("@annotation(com.kk.ddd.support.annotation.AccessCondition)")
  public Object advice(ProceedingJoinPoint point) throws Throwable {
    log.info("Method advice at '{}'.", point.getStaticPart());
    var condition =
        ((MethodSignature) point.getSignature())
            .getMethod()
            .getAnnotation(AccessCondition.class)
            .value();
    var backup = AccessConditionHelper.replay(condition);
    try {
      return point.proceed();
    } finally {
      AccessConditionHelper.restore(backup);
    }
  }
}
