package com.kkk.op.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求方是否可访问方法条件检查切面注解 <br>
 * 建议加在controller方法上，也可以加在application service方法上；<br>
 * 注意不支持异步，但支持顺序执行多次校验。
 *
 * @author KaiKoo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessCondition {

  /**
   * 逻辑条件组合表达式（支持 &&、||、!） <br>
   * 注释类默认参数名为value，此时不需要手动声明参数名，或者不添加参数名则默认为value。
   */
  String value();
}
