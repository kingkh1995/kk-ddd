package com.kkk.op.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * QueryService标识注解 <br>
 *
 * @author KaiKoo
 */
@Validated
@Service
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueryService {}
