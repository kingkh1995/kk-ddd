package com.kk.ddd.support.grl;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author mm
 */
@Slf4j
public class TokenClient extends ClusterClient implements Runnable {

  private final TokenClientConfig clientConfig;
  private final Object mutex = new Object();
  private final Stopwatch stopwatch;
  private final AtomicInteger counter = new AtomicInteger();
  private String currentServer;
  private volatile boolean flag;

  public TokenClient(TokenClientConfig clientConfig) {
    super(clientConfig);
    this.clientConfig = clientConfig;
    this.stopwatch = Stopwatch.createUnstarted();
  }

  @Override
  public void run() {
    synchronized (this.mutex) {
      while (isRunning()) {
        try {
          doCheckServer();
        } catch (Exception e) {
          log.error("Failed to check server, continue loop.", e);
        }
        var remained = counter.get();
        var outdate = isOutdate();
        // 1. 扣减成了负数，表示需要立即拉取token
        // 2. outdata，表示之前拉取的token全部已过期
        // 1&2. 在最后时刻来了大量的突发流量，然后到fetch token时当前时间窗刚好走完了，但是这个时候实际是已经占了后一个时间窗的token。
        // 故这种情况应该不允许存在，即扣减到负数的时候，需要重置时间窗，目前server端不允许burst，故是合理的。
        if (remained <= 0 && outdate) {
          log.warn("[BUG] remained <= 0 && outdate");
        }
        if (remained <= 0 || outdate) {
          acquireFromServer(
              this.clientConfig.getPermitsAcquiredPerRequest() - (outdate ? 0 : remained),
              this.currentServer);
          this.counter.set(this.clientConfig.getPermitsAcquiredPerRequest());
          this.stopwatch.reset().start();
        }
        flag = true;
        try {
          this.mutex.wait(500L);
        } catch (InterruptedException e) {
          log.warn("TokenClient run interrupted, stopping.");
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }

  /** Gracefully stop the background refill thread and wake up the waiting loop. */
  @Override
  public void stop() {
    super.stop();
    synchronized (this.mutex) {
      this.mutex.notify();
    }
  }

  @Override
  protected void doCheckServer() {
    super.doCheckServer();
    this.currentServer = findServer(this.clientConfig.getKey());
  }

  public boolean canAcquire(int permits) {
    return permits <= this.clientConfig.getMaxPermitsPerRequest();
  }

  public boolean tryAcquire(int permits) {
    if (permits <= 0 || !canAcquire(permits)) {
      return false;
    }
    synchronized (this.mutex) {
      var canAcquire = canAcquire();
      // 本地允许扣减成负数，支持预消费token，reject或者wait都不合理（），参考令牌桶的思想。
      if (!(flag = canAcquire && counter.addAndGet(-permits) > 0)) {
        // 唤醒fetch
        this.mutex.notify();
      }
      return canAcquire;
    }
  }

  private boolean isOutdate() {
    return stopwatch.elapsed(TimeUnit.MICROSECONDS) >= 1_000_000L;
  }

  private boolean canAcquire() { // to fail-fast don't use permits
    return isRunning() && flag && counter.get() > 0 && !isOutdate();
  }

  private void acquireFromServer(int permits, String address) {
    // todo.. RPC to server node to acquire permits
  }
}
