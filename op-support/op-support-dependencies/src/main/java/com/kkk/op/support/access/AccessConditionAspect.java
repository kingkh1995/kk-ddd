package com.kkk.op.support.access;

import com.kkk.op.support.annotation.AccessCondition;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AccessCondition切面 <br>
 * 辅助QueryService切面实现访问条件检查，在整个方法内QueryService的调用都会触发校验。
 *
 * @author KaiKoo
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 设置级别低于@BaseControllerAspect切面
@Aspect
public class AccessConditionAspect {

  @Around("@annotation(com.kkk.op.support.annotation.AccessCondition)")
  public Object advice(ProceedingJoinPoint point) throws Throwable {
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
