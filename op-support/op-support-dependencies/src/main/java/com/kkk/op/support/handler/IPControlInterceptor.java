package com.kkk.op.support.handler;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * IP限流，使用 Guava Cache & RateLimiter
 *
 * @author KaiKoo
 */
@Slf4j
public class IPControlInterceptor implements HandlerInterceptor {

  /** 限流开关 */
  private boolean controlSwitch;

  public IPControlInterceptor(boolean controlSwitch) {
    this.controlSwitch = controlSwitch;
  }

  private static final LoadingCache<String, RateLimiter> CACHE =
      // 使用caffeine同步加载缓存
      Caffeine.newBuilder()
          // 30秒未访问则过期
          .expireAfterAccess(30L, TimeUnit.SECONDS)
          // 设置为软引用，在内存不足时回收缓存
          .softValues()
          // 需要设置一个合适的初始容量，因为扩容消耗很大
          .initialCapacity(1 << 10)
          // 需要设置最大容量，软引用对象数量不能太多，对性能有影响
          .maximumSize(1 << 14)
          // 使用CacheLoader初始化RateLimiter，限制每秒访问一次
          .build(key -> RateLimiter.create(1D));

  // 对写请求做IP限流
  private static final Set<HttpMethod> METHODS =
      Collections.unmodifiableSet(
          EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE));

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 判断限流开关是否打开
    if (!controlSwitch) {
      return true;
    }
    // 判断请求方式
    var method = request.getMethod();
    if (!METHODS.contains(HttpMethod.valueOf(method))) {
      return true;
    }
    log.debug("IP-Control cache size '{}'.", CACHE.estimatedSize());
    var ip = getRealIp(request);
    // 判断是否流量超出
    if (CACHE.get(ip).tryAcquire()) {
      return true;
    }
    log.warn("Request '{}({}:{})' blocked by IP-Control!", ip, method, request.getRequestURI());
    throw IPControlBlockedException.INSTANCE;
  }

  private static String getRealIp(HttpServletRequest request) {
    var ip = request.getHeader("x-forwarded-for");
    if (isIPValid(ip)) {
      // 多次反向代理后会有多个ip值，第一个ip才是真实ip
      ip = ip.split(",")[0];
    }
    if (isIPValid(ip)) {
      return ip;
    }
    ip = request.getHeader("Proxy-Client-IP");
    if (isIPValid(ip)) {
      return ip;
    }
    ip = request.getHeader("WL-Proxy-Client-IP");
    if (isIPValid(ip)) {
      return ip;
    }
    ip = request.getHeader("HTTP_CLIENT_IP");
    if (isIPValid(ip)) {
      return ip;
    }
    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    if (isIPValid(ip)) {
      return ip;
    }
    ip = request.getHeader("X-Real-IP");
    if (isIPValid(ip)) {
      return ip;
    }
    ip = request.getRemoteAddr();
    return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
  }

  // 判断IP是否合法，仅简单判断
  private static boolean isIPValid(String ip) {
    return !(ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip));
  }

  public static class IPControlBlockedException extends RuntimeException {
    public static final IPControlBlockedException INSTANCE = new IPControlBlockedException();

    private IPControlBlockedException() {
      super("IP-Control blocked!");
    }
  }
}