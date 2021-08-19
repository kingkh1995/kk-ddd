package com.kkk.op.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller层注解 <br>
 * 注意：@Component及相关注解需要显式在类上定义，支持注解上注解的方式，但是无法被继承，添加@Inherited无效。
 *
 * @author KaiKoo
 */
@Validated // 校验 @PathVariable @RequestParam 需要添加 @Validated 注解
@RestController
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseController {}
