package com.kkk.op.support.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 应用层 query service 标识注解
 *
 * @author KaiKoo
 */
@Validated
@Service
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueryService {}
