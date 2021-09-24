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
public @interface DegradedService {}
