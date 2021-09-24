package com.kkk.op.support.aspect;

import com.kkk.op.support.annotation.MockResource;
import com.kkk.op.support.tool.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * mock切面实现 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@Order // 可以不添加@Order注解，默认级别为最低
@Aspect
public class MockResourceAspect extends AbstractMethodAspect {

  @Override
  @Pointcut("@annotation(com.kkk.op.support.annotation.MockResource)")
  protected void pointcut() {}

  @Override
  public boolean onBefore(JoinPoint point) {
    log.info("Always forbid when mock!");
    return false;
  }

  @Override
  public Object getOnForbid(JoinPoint point) {
    var signature = (MethodSignature) point.getSignature();
    var returnType = signature.getReturnType();
    // 获取注释值
    var method = signature.getMethod();
    var resource = method.getAnnotation(MockResource.class);
    // 判断注释参数
    var returnDefault = true;
    // mockClass默认为被代理类 target默认为被代理对象
    Class<?> mockClass;
    Object target;
    if (Object.class.equals(resource.mockClass())) {
      mockClass = signature.getDeclaringType();
      target = point.getTarget();
    } else {
      mockClass = resource.mockClass();
      target = null; // 调用静态方法则target传入null即可
      returnDefault = false;
    }
    // mockMethod默认为原方法名，指定mockClass时要求必须是静态方法
    String mockMethod;
    if ("".equals(resource.mockMethod())) {
      mockMethod = method.getName();
    } else {
      mockMethod = resource.mockMethod();
      returnDefault = false;
    }
    log.info("returnType = {}, returnDefault = {}.", returnType.getCanonicalName(), returnDefault);
    // 两个属性均为空则返回默认值
    if (returnDefault) {
      return ClassUtil.getDefault(returnType);
    }
    // 执行mock调用并返回结果
    try {
      log.info("Call mock, class = {}, method = {}.", mockClass.getCanonicalName(), mockMethod);
      var targetMethod = mockClass.getDeclaredMethod(mockMethod, method.getParameterTypes());
      targetMethod.trySetAccessible();
      return targetMethod.invoke(target, point.getArgs());
    } catch (Throwable e) {
      // 如果执行mock方法异常则抛出cause
      if (e.getCause() instanceof RuntimeException cause) {
        throw cause;
      }
      if (e.getCause() instanceof Error cause) {
        throw cause;
      }
      // 受检查异常（反射异常）包装成MockException
      throw new MockException(e);
    }
  }

  @Override
  public void onAfter(JoinPoint point, boolean permitted, boolean thrown, Object result) {
    // onBefore返回false，故在onComplete中打印日志
    log.info("Mock return '{}'.", result);
  }

  public class MockException extends RuntimeException {
    MockException(Throwable cause) {
      super(cause);
    }
  }
}
