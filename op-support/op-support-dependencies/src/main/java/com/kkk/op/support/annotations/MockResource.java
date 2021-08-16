package com.kkk.op.support.annotations;

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
  /**
   * mock返回值，不为空则返回json字符串反序列化的结果，为空则根据方法返回值类型决定默认返回值。 <br>
   * 基本数据类型：void-无返回值；数字-0；布尔值-false； <br>
   * 包装类、枚举及对象：null；<br>
   * 数组：空数组；<br>
   * 集合：空集合。<br>
   */
  String mockJson() default "";

  /** mock时调用的类，如果为空则默认为当前被代理类。 */
  Class<?> mockClass() default Object.class;

  /** mock类中被调用的方法，参数需与原方法一致，且如不在同一个类中必须声明为静态方法，如果为空则默认为原方法名。 */
  String mockMethod() default "";
}
