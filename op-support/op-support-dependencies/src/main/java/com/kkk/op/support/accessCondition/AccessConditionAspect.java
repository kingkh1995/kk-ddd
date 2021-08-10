package com.kkk.op.support.accessCondition;

import com.kkk.op.support.aspect.AbstractMethodAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * AccessCondition切面 <br>
 * todo...
 *
 * @author KaiKoo
 */
@Aspect
@Order(0)
public class AccessConditionAspect extends AbstractMethodAspect {

  @Override
  @Pointcut("@annotation(com.kkk.op.support.accessCondition.AccessCondition)")
  protected void pointcut() {}
}
