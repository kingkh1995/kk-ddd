package com.kkk.op.support.aspect;

import com.kkk.op.support.accessCondition.AccessConditionChecker;
import com.kkk.op.support.accessCondition.AccessConditionForbiddenException;
import com.kkk.op.support.bean.LocalRequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * AccessCondition的设计是将QueryService的方法作为切点，而@AccessCondition注解在QueryService的调用方处。 <br>
 * 因为查询方法的访问条件是由调用方决定的 <br>
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置级别最高
@Aspect
@RequiredArgsConstructor
public class QueryServiceAspect extends AbstractMethodAspect {

  private final AccessConditionChecker checker;

  @Override
  @Pointcut("@within(com.kkk.op.support.base.QueryService)")
  protected void pointcut() {}

  @Override
  public void onSuccess(JoinPoint point, Object result) {
    // 后置增强处理，成功查询出结果之后再判断是否允许访问
    var checkPass =
        this.checker.analyzeThenCheck(
            result, LocalRequestContextHolder.getLocalRequestContext().replayAccessCondition());
    // 禁止访问则抛出异常
    if (!checkPass) {
      throw AccessConditionForbiddenException.INSTANCE;
    }
  }
}
