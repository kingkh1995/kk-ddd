package com.kkk.op.support.aspect;

import com.google.common.base.Throwables;
import com.kkk.op.support.annotations.MockResource;
import com.kkk.op.support.bean.Uson;
import com.kkk.op.support.tools.ReflectUtil;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

/**
 * mock切面实现 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Order // 可以不添加@Order注解，默认级别为最低
@Aspect
public class MockResourceAspect extends AbstractMethodAspect {

  private final Uson uson;

  public MockResourceAspect(Uson uson) {
    this.uson = Objects.requireNonNull(uson);
  }

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
    var signature = (MethodSignature) point.getSignature();
    var returnType = signature.getReturnType();
    log.info("return type: {}", returnType.getCanonicalName());
    // 获取注释值
    var method = signature.getMethod();
    var resource = method.getAnnotation(MockResource.class);
    // 判断注释参数
    var returnDefault = true;
    // mockClass默认为自身
    Class<?> mockClass;
    Object target;
    if (Object.class.equals(resource.mockClass())) {
      mockClass = signature.getDeclaringType();
      target = point.getTarget();
    } else {
      mockClass = resource.mockClass();
      // 调用其他类的静态方法时target传入null即可
      target = null;
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
    // 两个属性均为空则返回默认值
    if (returnDefault) {
      return ReflectUtil.getDefault(returnType);
    }
    // 执行mock调用并返回结果
    try {
      return mockClass
          .getMethod(mockMethod, method.getParameterTypes())
          .invoke(target, point.getArgs());
    } catch (Exception e) {
      // 非检查时异常直接抛出，受检查异常（反射异常）包装成RuntimeException。
      Throwables.throwIfUnchecked(e);
      throw new MockException(e);
    }
  }

  @Override
  public void onComplete(JoinPoint point, boolean permitted, boolean thrown, Object result) {
    log.info("mock return: [{}]!", this.uson.writeJson(result));
  }

  public class MockException extends RuntimeException {
    public MockException(Throwable cause) {
      super(cause);
    }
  }
}
