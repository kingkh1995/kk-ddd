package com.kk.ddd.support.aspect;

import com.kk.ddd.support.annotation.DegradedService;
import com.kk.ddd.support.util.ApplicationContextAwareSingleton;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 服务降级拦截切面 <br>
 * 单机实现，分布式实现请使用RedissonClient
 *
 * @author KaiKoo
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 1) // 优先级高于@MockResource切面
@Aspect
public class DegradedServiceAspect extends ApplicationContextAwareSingleton {

  private Map<Class<?>, DegradedContext> map;
  private final Executor executor;

  public DegradedServiceAspect(int healthInterval) {
    // 并没有创建线程池，而是通过内部类Delayer延迟提交任务到执行线程池。
    this.executor = CompletableFuture.delayedExecutor(healthInterval, TimeUnit.SECONDS);
  }

  private static class DegradedContext {

    Object target;
    Class<? extends Throwable>[] permittedThrowables;
    Method health;
    Map<Method, Method> callbackMethods = Collections.emptyMap();

    /** 降级标记，使用原子类型，volatile只能保证每次取到的值都是最新值 */
    final AtomicBoolean degraded = new AtomicBoolean(false);

    boolean permit(Throwable e) {
      return Arrays.stream(permittedThrowables).anyMatch(c -> c.isInstance(e));
    }
  }

  @Override
  public void afterSingletonsInstantiated() {
    if (map != null) {
      return;
    }
    // 初始化时即解析并加载
    var objects =
        this.getApplicationContext().getBeansWithAnnotation(DegradedService.class).values();
    map = new IdentityHashMap<>(objects.size());
    for (var obj : objects) {
      // 被ioc管理的是spring代理对象，需要获取到被代理对象
      if (AopUtils.isAopProxy(obj)) {
        try {
          init(AopProxyUtils.getSingletonTarget(obj));
        } catch (Exception e) {
          log.warn("DegradedContext init error!", e);
        }
      }
    }
  }

  private void init(Object target) throws Exception {
    var clazz = target.getClass();
    var context = new DegradedContext();
    context.target = target;
    // 开始解析
    var degradedService = clazz.getDeclaredAnnotation(DegradedService.class);
    context.permittedThrowables = degradedService.permittedThrowables();
    // 获取心跳方法，并执行一次心跳检测，以验证是否存在。
    context.health = clazz.getDeclaredMethod(degradedService.health());
    context.health.trySetAccessible();
    context.health.invoke(target);
    // 解析回调方法
    var callbackClass = degradedService.callbackClass();
    if (callbackClass != Object.class) {
      var declaredMethods = clazz.getDeclaredMethods();
      context.callbackMethods = new HashMap<>(declaredMethods.length, 1.0f);
      for (var method : declaredMethods) {
        // 查找目标类所有public方法在回调工具类中对应的public static方法）
        if (Modifier.isPublic(method.getModifiers())) {
          try {
            var callbackClassDeclaredMethod =
                callbackClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            if (!Modifier.isPublic(callbackClassDeclaredMethod.getModifiers())
                || !Modifier.isStatic(callbackClassDeclaredMethod.getModifiers())) {
              log.warn(
                  "DegradedContext init callback method error, method '{}' should be [public static]!",
                  callbackClassDeclaredMethod);
              continue;
            }
            // public static方法不需要setAccessible
            context.callbackMethods.put(method, callbackClassDeclaredMethod);
          } catch (Exception e) {
            // 不存在对应方法或反射失败，打印日志后继续
            log.warn("DegradedContext init callback method error!", e);
          }
        }
      }
    }
    map.put(clazz, context);
  }

  @Around("@within(com.kk.ddd.support.annotation.DegradedService)")
  public Object advice(ProceedingJoinPoint point) throws Throwable {
    log.info("Method advice at '{}'.", point.getStaticPart());
    // 获取上下文对象，暂时要求上下文对象必须存在
    var context = map.get(point.getSignature().getDeclaringType());
    if (context.degraded.get()) {
      // 执行降级操作
      return doDegrade(
          context, ((MethodSignature) point.getSignature()).getMethod(), point.getArgs());
    }
    try {
      return point.proceed();
    } catch (Throwable e) { // 基础功能的代码可以捕获Throwable
      // 抛出异常前，降级并触发心跳检测
      doThrow(e, context);
      throw e;
    }
  }

  private Object doDegrade(DegradedContext context, Method method, Object[] args) throws Throwable {
    var callbackMethod = context.callbackMethods.get(method);
    if (callbackMethod == null) {
      throw DegradedServiceException.INSTANCE;
    }
    log.info(
        "Degraded service '{}' invoke method '{}'.",
        context.target.getClass().getCanonicalName(),
        callbackMethod);
    return callbackMethod.invoke(null, args);
  }

  private void doThrow(Throwable e, DegradedContext context) {
    // 异常类型不被允许情况下 ，CAS设置降级标记，成功则触发心跳检测
    if (!context.permit(e) && context.degraded.compareAndSet(false, true)) {
      healthCheck(context);
    }
  }

  private void healthCheck(DegradedContext context) {
    // 延迟执行
    executor.execute(
        () -> {
          try {
            context.health.invoke(context.target);
            log.info(
                "Degraded service '{}' health check succeed!",
                context.target.getClass().getCanonicalName());
            // 心跳正常，进行恢复
            context.degraded.set(false);
          } catch (Exception e) { // 不应该捕获Throwable，因为Error发生时也不应该继续恢复心跳。
            log.warn(
                "Degraded service '{}' health check throw!",
                context.target.getClass().getCanonicalName(),
                e);
            // 心跳失败，继续检测
            healthCheck(context);
          }
        });
  }

  public static class DegradedServiceException extends RuntimeException {

    public static final DegradedServiceException INSTANCE =
        new DegradedServiceException("Blocked by degraded service!");

    public DegradedServiceException(String message) {
      super(message);
    }
  }
}
