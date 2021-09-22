package com.kkk.op.support.access;

import com.kkk.op.support.annotation.AccessCondition;
import com.kkk.op.support.aspect.AbstractMethodAspect;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AccessCondition切面 <br>
 * 辅助QueryService切面实现访问条件检查
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 设置级别低于@BaseControllerAspect切面
@Aspect
public class AccessConditionAspect extends AbstractMethodAspect {

  @Override
  @Pointcut("@annotation(com.kkk.op.support.annotation.AccessCondition)")
  protected void pointcut() {}

  @Override
  public boolean onBefore(JoinPoint point) {
    // 前置增强从注释中取出条件保存至工具类
    var condition =
        ((MethodSignature) point.getSignature())
            .getMethod()
            .getAnnotation(AccessCondition.class)
            .value();
    AccessConditionHelper.capture(condition);
    return true;
  }

  @Override
  public void onThrow(JoinPoint point, Throwable e) {
    // 异常时增强，清空accessCondition
    AccessConditionHelper.reset();
  }
}
