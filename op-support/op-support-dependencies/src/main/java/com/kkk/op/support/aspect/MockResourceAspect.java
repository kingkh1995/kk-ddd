package com.kkk.op.support.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

/**
 * mock切面实现 <br>
 * 判断如下：<br>
 * 1、mockJson不为空，则反序列化，为空则进行下一步； <br>
 * 2、mockClass不为空，则利用反射从Class对象中根据mockMethod指定的方法名与找到原方法参数一致的静态方法，如果mockMethod为空则默认为原方法名，否则执行下一步；
 * <br>
 * 3、mockClass为空则从当前类中根据mockMethod指定的方法名与找到原方法参数一致的方法，mockMethod为空则执行下一步； <br>
 * 4、如果三个参数均为空，则返回默认值。 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Order // 可以不添加@Order注解，默认级别为最低
@Aspect
public class MockResourceAspect extends AbstractMethodAspect {

  @Override
  @Pointcut("@annotation(com.kkk.op.support.annotations.MockResource)")
  protected void pointcut() {}

  @Override
  public boolean onBefore(JoinPoint point) {
    log.info("always forbid when mock!");
    return false;
  }

  @Override
  public Object getOnForbid(JoinPoint point) {
    var o = super.getOnForbid(point);
    var signature = (MethodSignature) point.getSignature();
    var returnType = signature.getReturnType();
    // todo... 待完善
    if (returnType.isPrimitive()) {
      // 处理基本数据类型
      if ("boolean".equals(returnType.getName())) {
        o = false;
      } else if ("char".equals(returnType.getName())) {
        o = '-';
      } else {
        o = 0;
      }
    } else if (returnType.isArray()) {
      //  处理数组类型，分为基本数据类型数组和对象类数组
    }
    log.info("return type: [{}], mock return: [{}]!", returnType.getSimpleName(), o);
    return o;
  }
}
