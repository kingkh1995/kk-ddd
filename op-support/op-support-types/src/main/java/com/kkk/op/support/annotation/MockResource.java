package com.kkk.op.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mock注解 参考@SentinelResource设计 <br>
 *
 * @author KaiKoo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MockResource {

  /** 本意是想添加一个value，用来传入json字符串，然后反序列化为指定的类型，但是反序列化需要使用泛型，而切面无法使用，故无法实现。 */
  @Deprecated
  String value() default "";

  /** mock时调用的类，如果为空则默认为当前被代理类。 */
  Class<?> mockClass() default Object.class;

  /** mock类中被调用的方法，参数需与原方法一致，且如不在同一个类中必须声明为静态方法，如果为空则默认为原方法名。 */
  String mockMethod() default "";
}
