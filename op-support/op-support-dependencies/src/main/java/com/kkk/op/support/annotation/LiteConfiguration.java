package com.kkk.op.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类默认为Full模式，bean方法被调用返回的仍然是同一个对象，Lite模式下则每次都会创建新的对象，能提高启动速度，建议都使用Lite模式。 <br>
 *
 * @author KaiKoo
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Configuration(proxyBeanMethods = false)
public @interface LiteConfiguration {}
