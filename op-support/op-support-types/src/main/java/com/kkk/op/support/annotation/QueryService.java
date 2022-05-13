package com.kkk.op.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 领域服务查询模型QueryService标识注解 <br>
 * CQRS: 命令查询责任分离，命令(写操作)和查询(读操作)使用不同的数据模型，通过领域事件将命令模型中的变更传播到查询模型中。 <br>
 *
 * @author KaiKoo
 */
@Validated
@Service
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueryService {}
