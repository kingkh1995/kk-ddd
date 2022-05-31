package com.kk.ddd.support.aspect;

import org.aspectj.lang.JoinPoint;
import org.springframework.lang.Nullable;

/**
 * 方法增强处理器接口（参数均为JoinPoint，执行proceed在模板方法中）<br>
 *
 * @author KaiKoo
 */
public interface MethodAdviceHandler {

  /**
   * 目标方法执行之前的操作，并判断目标方法是否允许执行，默认返回true，即允许执行
   *
   * @param point 目标方法的连接点
   * @return 当返回 false 时，此时会先调用 getOnForbid，方法获得被禁止执行时的返回值，然后调用 onComplete 结束切面
   */
  default boolean onBefore(JoinPoint point) {
    return true;
  }

  /**
   * 禁止调用目标方法时（即 onBefore 返回 false），执行该方法获得返回值，默认返回 null
   *
   * @param point 目标方法的连接点
   * @return 禁止调用目标方法时的返回值
   */
  default Object getOnForbid(JoinPoint point) {
    return null;
  }

  /**
   * 目标方法抛出异常时，执行的动作（如消息通知、打印日志等）
   *
   * @param point 目标方法的连接点
   * @param e 抛出的异常
   */
  default void onThrow(JoinPoint point, Throwable e) {}

  /**
   * 获得抛出异常时的返回值，默认直接抛出异常不进行捕获
   *
   * @param point 目标方法的连接点
   * @param e 抛出的异常
   * @return 抛出异常时的返回值
   */
  default Object getOnThrow(JoinPoint point, Throwable e) throws Throwable {
    throw e;
  }

  /**
   * 目标方法成功时执行的动作 permitted 为 true，thrown 为 false
   *
   * @param point 目标方法的连接点
   * @param result 执行获得的结果
   */
  default void onSucceed(JoinPoint point, Object result) {}

  /**
   * 目标方法完成时，执行的动作，同 @After 但是可以操作返回值（需注意若 permitted 为 false 或 thrown 为 true 时默认返回值会为 null）
   *
   * @param point 目标方法的连接点
   * @param permitted 目标方法是否被允许执行
   * @param thrown 目标方法执行时是否抛出异常
   * @param result 执行获得的结果
   */
  default void onAfter(
      JoinPoint point, boolean permitted, boolean thrown, @Nullable Object result) {}
}
