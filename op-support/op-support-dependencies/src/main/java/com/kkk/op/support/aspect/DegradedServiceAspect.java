package com.kkk.op.support.aspect;

import com.kkk.op.support.annotation.DegradedService;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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
public class DegradedServiceAspect extends AbstractMethodAspect {

  private final ConcurrentHashMap<Class<?>, DegradedContext> map;
  private final Executor executor;

  public DegradedServiceAspect(int healthInterval) {
    this.map = new ConcurrentHashMap<>();
    this.executor = CompletableFuture.delayedExecutor(healthInterval, TimeUnit.SECONDS);
  }

  /** 降级服务上下文对象 */
  private static class DegradedContext {

    Object target;
    Method health;
    Class<? extends Throwable>[] permittedThrowables;
    Map<String, Method> callbackMethods = Collections.emptyMap();
    volatile boolean degraded = false;

    boolean permit(Throwable e) {
      return Arrays.stream(permittedThrowables).anyMatch(c -> c.isInstance(e));
    }
  }

  @Override
  @Around("@within(com.kkk.op.support.annotation.DegradedService)")
  protected void pointcut() {}

  @Override
  public boolean onBefore(JoinPoint point) {
    // 被降级则禁止
    return Optional.ofNullable(map.get(point.getSignature().getDeclaringType()))
        .map(context -> !context.degraded)
        .orElse(true);
  }

  @Override
  public Object getOnForbid(JoinPoint point) {
    // 执行降级操作
    return doDegrade(
        point.getSignature().getDeclaringType(),
        ((MethodSignature) point.getSignature()).getMethod(),
        point.getArgs());
  }

  @Override
  public void onThrow(JoinPoint point, Throwable e) {
    // 异常抛出前触发心跳检测
    // 获取上下文对象或初始化
    var clazz = point.getSignature().getDeclaringType();
    var context = map.getOrDefault(clazz, init(point.getTarget()));
    // 判断是否允许异常类型是否被允许
    if (!context.permit(e)) {
      // 降级
      context.degraded = true;
      // 心跳检测
      healthCheck(clazz);
    }
  }

  private Object doDegrade(Class<?> clazz, Method method, Object[] args) {
    var context = map.get(clazz);
    var callbackMethod = context.callbackMethods.get(method.toString());
    // 存在回调方法则执行，否则抛出指定异常
    if (callbackMethod == null) {
      throw DegradedServiceException.INSTANCE;
    }
    log.info("Degraded service '{}' invoke method '{}'.", clazz.getCanonicalName(), callbackMethod);
    try {
      return callbackMethod.invoke(null, args);
    } catch (Exception e) {
      throw new DegradedServiceException(e);
    }
  }

  private DegradedContext init(Object target) {
    var clazz = target.getClass();
    var context = new DegradedContext();
    context.target = target;
    // 解析
    var degradedService = clazz.getDeclaredAnnotation(DegradedService.class);
    try {
      context.health = clazz.getDeclaredMethod(degradedService.health());
    } catch (NoSuchMethodException e) {
      throw new DegradedServiceException(e);
    }
    context.health.setAccessible(true); // must succeed or throw
    context.permittedThrowables = degradedService.permittedThrowables();
    var callbackClass = degradedService.callbackClass();
    if (callbackClass != Object.class) {
      var declaredMethods = clazz.getDeclaredMethods();
      context.callbackMethods = new HashMap<>(declaredMethods.length, 1.0f);
      for (var method : declaredMethods) {
        // 添加回调工具类中存在的对应public方法
        if ((method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
          try {
            var callbackClassDeclaredMethod =
                callbackClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            callbackClassDeclaredMethod.trySetAccessible();
            context.callbackMethods.put(method.toString(), callbackClassDeclaredMethod);
          } catch (Exception e) {
            // 失败或不存在对应方法，打印日志继续
            log.warn("DegradedContext init error!", e);
          }
        }
      }
    }
    // put可能出现并发，如果其他线程已经put了，直接返回。
    return Optional.ofNullable(map.putIfAbsent(clazz, context)).orElse(context);
  }

  private void healthCheck(Class<?> clazz) {
    executor.execute(
        () -> {
          var context = map.get(clazz);
          try {
            context.health.invoke(context.target);
            log.info("Degraded service '{}' health check succeed!", clazz.getCanonicalName());
            // 心跳正常，进行恢复
            context.degraded = false;
          } catch (Throwable e) {
            log.warn("Degraded service '{}' health check throw!", clazz.getCanonicalName(), e);
            // 心跳失败，继续检测
            healthCheck(clazz);
          }
        });
  }

  public static class DegradedServiceException extends RuntimeException {

    public static final DegradedServiceException INSTANCE =
        new DegradedServiceException("Blocked by degraded service!");

    public DegradedServiceException(String message) {
      super(message);
    }

    public DegradedServiceException(Throwable cause) {
      super(cause);
    }
  }
}
