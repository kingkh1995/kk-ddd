package com.kkk.op.support.aspect;

import com.kkk.op.support.accessCondition.AccessConditionChecker;
import com.kkk.op.support.bean.BaseRequestContextHolder;
import com.kkk.op.support.exception.BusinessException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * AccessCondition核心逻辑切面实现，对查询方法做访问控制，需要@AccessCondition注解辅助；<br>
 * 原因是不同调用方对查询方法的访问要求是不一样的，所以条件的定义需要移到QueryService的外层。 <br>
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置级别最高
@Aspect
public class QueryServiceAspect extends AbstractMethodAspect {

  private final AccessConditionChecker checker;

  public QueryServiceAspect(@Autowired AccessConditionChecker checker) {
    this.checker = Objects.requireNonNull(checker);
  }

  @Override
  @Pointcut("@within(com.kkk.op.support.base.QueryService)")
  protected void pointcut() {}

  @Override
  public void onSuccess(JoinPoint point, Object result) {
    // 后置增强处理，成功查询出结果之后判断是否允许访问查询出来的数据
    boolean canAccess =
        this.checker.analyzeThenCheck(
            result, BaseRequestContextHolder.getBaseRequestContext().getAccessCondition());
    // 禁止访问则抛出异常
    if (!canAccess) {
      throw new BusinessException("access forbidden!");
    }
  }
}
