package com.kkk.op.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求方是否可访问方法条件检查切面注解，需要加在controller方法上。
 *
 * @author KaiKoo
 */
@Retention(RetentionPolicy.RUNTIME) // 在@BaseController注解切面之后执行
@Target(ElementType.METHOD)
public @interface AccessCondition {

  /**
   * 逻辑条件组合表达式（支持 &&、||、!） <br>
   * 注释类默认参数名为value，此时不需要手动声明参数名，或者不添加参数名则默认为value。
   */
  String value();
}
