package com.kkk.op.support.aspect;

import com.kkk.op.support.annotation.DegradedService;
import com.kkk.op.support.exception.BusinessException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * 服务降级拦截切面 <br>
 *
 * @author KaiKoo
 */
@Aspect
public class DegradedServiceAspect extends AbstractMethodAspect {

  @Override
  @Pointcut("@within(com.kkk.op.support.annotation.DegradedService)")
  protected void pointcut() {}

  private final RedissonClient redissonClient;
  private final DegradedServiceConfig config;
  private final ConcurrentHashMap<Class<?>, DegradedContext> map;

  public DegradedServiceAspect(RedissonClient redissonClient, DegradedServiceConfig config) {
    this.redissonClient = redissonClient;
    this.config = config;
    this.map = new ConcurrentHashMap<>();
  }

  private static class DegradedContext {
    RRateLimiter rateLimiter;
    volatile boolean degraded = false;
    Method health;
    Class<?> callbackClass;
  }

  public record DegradedServiceConfig(
      RateType rateType,
      long rate,
      long rateInterval,
      RateIntervalUnit rateIntervalUnit,
      long healthInterval,
      TimeUnit healthIntervalUnit) {}

  @Override
  public boolean onBefore(JoinPoint point) {
    return Optional.ofNullable(map.get(point.getSignature().getDeclaringType()))
        .map(degradedContext -> degradedContext.degraded)
        .orElse(true);
  }

  @Override
  public Object getOnForbid(JoinPoint point) {
    // todo... 调用回调类的方法或直接抛出异常
    throw DegradedServiceException.INSTANCE;
  }

  @Override
  public Object getOnThrow(JoinPoint point, Throwable e) throws Throwable {
    if (!(e instanceof BusinessException)) {
      doAcquire(point);
    }
    throw e;
  }

  private void doAcquire(JoinPoint point) throws Throwable {
    var signature = (MethodSignature) point.getSignature();
    var clazz = signature.getDeclaringType();
    var context = map.get(clazz);
    if (context == null) {
      // 初始化
      context = new DegradedContext();
      if (map.putIfAbsent(clazz, context) != null) {
        //  put失败
        return;
      }
      // 回调类
      var annotation = signature.getMethod().getAnnotation(DegradedService.class);
      context.callbackClass =
          annotation.callbackClass() == Object.class ? null : annotation.callbackClass();
      // 心跳方法
      context.health = clazz.getDeclaredMethod(annotation.health());
      // 限流器
      context.rateLimiter =
          redissonClient.getRateLimiter("DSAR:" + signature.getDeclaringTypeName());
      if (!context.rateLimiter.trySetRate(
          this.config.rateType,
          this.config.rate,
          this.config.rateInterval,
          this.config.rateIntervalUnit)) {
        // 设置限流失败
        map.remove(clazz);
        context.rateLimiter.unlink();
        return;
      }
    }
    if (!context.rateLimiter.tryAcquire() && !context.degraded) {
      // 失败次数达到阈值且未被降级，则设置状态降级，并定时执行心跳
      context.degraded = true;
      // todo... 心跳检测
    }
  }

  public static class DegradedServiceException extends RuntimeException {
    public static final DegradedServiceException INSTANCE =
        new DegradedServiceException("Blocked by degraded service!");

    public DegradedServiceException(String message) {
      super(message);
    }
  }
}
