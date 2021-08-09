package com.kkk.op.support.accessCondition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求方是否可访问方法条件
 *
 * @author KaiKoo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AccessCondition {

  String DEFUALT = "default";

  /** 逻辑条件组合表达式（支持 &&、||、!） */
  String condition() default DEFUALT;
}
