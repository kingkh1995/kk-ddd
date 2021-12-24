package com.kkk.op.support.interceptor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * IP限流，使用 Guava Cache & RateLimiter
 *
 * @author KaiKoo
 */
@Slf4j
public class IPControlInterceptor implements HandlerInterceptor {

  /** 导入配置类Bean，实现动态刷新。 */
  private final IPControlProperties properties;

  private final LoadingCache<String, RateLimiter> cache;

  public IPControlInterceptor(IPControlProperties properties) {
    this.properties = properties;
    // 使用caffeine同步加载缓存
    this.cache =
        Caffeine.newBuilder()
            // 30秒未访问则过期
            .expireAfterAccess(1L, TimeUnit.SECONDS)
            // 设置为软引用，在内存不足时回收缓存
            .softValues()
            // 需要设置一个合适的初始容量，因为扩容消耗很大
            .initialCapacity(1 << 10)
            // 需要设置最大容量，软引用对象数量不能太多，对性能有影响
            .maximumSize(1 << 14)
            // 使用CacheLoader初始化RateLimiter
            .build(key -> RateLimiter.create(this.properties.getPermitsPerSecond()));
  }

  // 对写请求做IP限流
  private static final Set<HttpMethod> METHODS =
      Collections.unmodifiableSet(
          EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE));

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    log.info("config:{}, cache size: {}.", properties, cache.estimatedSize());
    // 判断是否限流控制
    if (properties.isDisabled()) {
      return true;
    }
    // 判断请求方式
    var method = request.getMethod();
    if (!METHODS.contains(HttpMethod.valueOf(method))) {
      return true;
    }
    var ip = getRealIp(request);
    // 判断是否流量超出
    if (cache.get(ip).tryAcquire()) {
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

  @Data
  @ConfigurationProperties("ip-control")
  public static class IPControlProperties {
    private boolean disabled = false;
    private double permitsPerSecond = 1D;
  }

  public static class IPControlBlockedException extends RuntimeException {
    public static final IPControlBlockedException INSTANCE = new IPControlBlockedException();

    private IPControlBlockedException() {
      super("IP-Control blocked!");
    }
  }
}
