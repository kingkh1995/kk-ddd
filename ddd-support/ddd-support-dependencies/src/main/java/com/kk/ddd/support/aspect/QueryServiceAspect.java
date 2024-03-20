package com.kk.ddd.support.aspect;

import com.kk.ddd.support.access.AccessConditionForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * AccessCondition的设计是将QueryService的方法作为切点，@AccessCondition注解则在QueryService的调用方处；<br>
 * 因为查询方法的访问条件是由调用方决定的，且使用后置切面是因为有些判断条件需要查询的结果。<br>
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置级别最高
@Aspect
@RequiredArgsConstructor
public class QueryServiceAspect extends AbstractMethodAspect {

  private final QueryServiceChecker checker;

  @Override
  @Pointcut("target(com.kk.ddd.support.core.QueryService)") // 拦截所有QueryService的实现类
  protected void pointcut() {}

  @Override
  public boolean onBefore(JoinPoint point) { // 前置增强，校验参数
    return checker.checkBefore(point.getTarget(), point.getArgs());
  }

  @Override
  public Object getOnForbid(JoinPoint point) {
    throw AccessConditionForbiddenException.INSTANCE;
  }

  @Override
  public void onSucceed(JoinPoint point, Object result) { // 后置增强，校验结果
    // 禁止访问则抛出异常
    if (!this.checker.checkAfter(point.getTarget(), result)) {
      throw AccessConditionForbiddenException.INSTANCE;
    }
  }
}
