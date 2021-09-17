package com.kkk.op.support.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

/**
 * 动态切面类基类 <br>
 * （使用模板方法模式：针对同一流程步骤固定，对特定步骤自定义。）
 *
 * @author KaiKoo
 */
@Slf4j
public abstract class AbstractMethodAspect implements MethodAdviceHandler {

  /** 切点，由子类实现，通过 @Pointcut 指定相关的注解 */
  protected abstract void pointcut();

  /**
   * 模板方法，对目标方法进行环绕增强处理，子类需通过 pointcut() 方法指定切点 <br>
   * Before After（always）AfterRunning（only success） AfterThrowing（only throw） <br>
   * Around 增强需要返回一个对象，其他类型增强均无返回值 <br>
   * 注意增强对类的内部调用是不起作用的，只对被代理类被调用的入口方法起作用。
   *
   * @param point 连接点
   * @return 方法执行返回值
   */
  @Around("pointcut()")
  public Object advice(ProceedingJoinPoint point) throws Throwable {
    log.info("Method advicing at '{}'.", point.getStaticPart());
    // 执行之前，判断是否被允许执行
    var permitted = this.onBefore(point);
    // 是否抛出了异常
    var thrown = false;
    // 方法返回值
    Object result = null;
    try {
      if (permitted) {
        // 目标方法被允许执行
        try {
          // 执行目标方法
          result = point.proceed();
          // 执行成功操作
          onSuccess(point, result);
        } catch (Throwable e) {
          // 抛出异常
          thrown = true;
          // 处理异常
          this.onThrow(point, e);
          // 抛出异常时的返回值
          result = this.getOnThrow(point, e);
        }
      } else {
        // 禁止执行时的返回值
        result = this.getOnForbid(point);
      }
    } finally {
      // 结束
      this.onComplete(point, permitted, thrown, result);
    }
    // 返回结果
    return result;
  }
}
