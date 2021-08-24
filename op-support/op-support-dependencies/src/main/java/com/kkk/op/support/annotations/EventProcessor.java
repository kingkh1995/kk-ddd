package com.kkk.op.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * 事件处理器标识注解 <br>
 * 因为注解要求能公用，所以参数只能定义为字符串，实现上建议使用枚举再转换为字符串
 *
 * @author KaiKoo
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface EventProcessor {

  /** 事件ID，为了减小复杂度，一个事件处理器只能对应一个事件 */
  String eventId();

  /** 指定状态 */
  String[] state() default {};

  /** 指定业务 */
  String[] biz() default {};

  /** 指定场景 */
  String[] scene() default {};
}
