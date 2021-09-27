package com.kkk.op.support.annotation;

import com.kkk.op.support.exception.BusinessException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 降级服务标识注解 <br>
 * 定时心跳检测，如果失败则服务降级，快速失败。
 *
 * @author KaiKoo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DegradedService {

  /** 定时调用的心跳方法（同一个类中且无参数），心跳失败则降级，心跳正常则恢复。 */
  String health() default "health";

  /** 允许抛出的异常类型 */
  Class<? extends Throwable>[] permittedThrowables() default {BusinessException.class};

  /** 降级时，会调用指定静态类的同名方法，如果不存在则默认抛出异常。 */
  Class<?> callbackClass() default Object.class;
}
