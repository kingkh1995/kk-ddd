package com.kkk.op.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller层注解 <br>
 * 注意：@Component及相关注解需要显式在类上定义，支持注解上注解的方式，但是无法被继承，添加@Inherited无效。
 *
 * @author KaiKoo
 */
@RestController
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseController {}
