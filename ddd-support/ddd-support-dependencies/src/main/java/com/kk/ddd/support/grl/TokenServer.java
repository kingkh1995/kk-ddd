package com.kk.ddd.support.grl;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
public class TokenServer extends ClusterServer {

  // todo... clean outdated
  private final ConcurrentHashMap<String, RateLimiter> rlMap = new ConcurrentHashMap<>();

  public TokenServer(TokenServerConfig serverConfig) {
    super(serverConfig);
  }

  public void register(String name, int permitsPerSecond) {
    rlMap.compute(
        name,
        (k, rateLimiter) -> {
          if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(permitsPerSecond);
          } else {
            rateLimiter.setRate(permitsPerSecond);
          }
          return rateLimiter;
        });
  }

  public int acquire(String name, int permits) {
    var rateLimiter = rlMap.get(name);
    if (rateLimiter == null || Double.compare(rateLimiter.getRate(), permits) < 0) {
      return 0;
    }
    return rateLimiter.tryAcquire(permits, Duration.ofMillis(1_000L)) ? permits : 0;
  }
}
