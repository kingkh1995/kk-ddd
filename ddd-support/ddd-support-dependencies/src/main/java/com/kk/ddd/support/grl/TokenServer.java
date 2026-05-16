package com.kk.ddd.support.grl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
public class TokenServer extends ClusterServer {

  private final Cache<String, RateLimiter> rlCache;

  public TokenServer(TokenServerConfig serverConfig) {
    super(serverConfig);
    this.rlCache =
        CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .recordStats()
            .build();
  }

  public void register(String name, int permitsPerSecond) {
    rlCache
        .asMap()
        .compute(
            name,
            (k, rl) -> {
              if (rl == null) {
                log.info(
                    "Create RateLimiter: name={}, permitsPerSecond={}", name, permitsPerSecond);
                return RateLimiter.create(permitsPerSecond);
              } else {
                rl.setRate(permitsPerSecond);
                return rl;
              }
            });
  }

  public boolean acquire(String name, int permits) {
    return Optional.ofNullable(rlCache.getIfPresent(name))
        .filter(rl -> permits <= (int) rl.getRate())
        .map(rl -> rl.tryAcquire(permits))
        .orElse(false);
  }
}
