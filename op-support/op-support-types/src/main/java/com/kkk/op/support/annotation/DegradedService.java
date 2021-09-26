package com.kkk.op.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 降级服务标识注解 <br>
 * todo... 捕获异常，心跳检测，at cache。
 *
 * @author KaiKoo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DegradedService {

  /** 被降级后，定时调用的心跳方法（同一个类中且无参数），心跳正常（不抛出异常）才恢复。 */
  String health();

  /** 降级时，会调用指定静态类的同名方法，如果不存在则默认抛出异常。 */
  Class<?> callbackClass() default Object.class;
}
