package com.kkk.op.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EntityRepository缓存标识注解
 * @author KaiKoo
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    // 默认为true
    boolean autoCaching() default true;

}
