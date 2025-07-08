package com.kk.ddd.support.grl;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <br>
 *
 * @author mm
 */
public class TokenClient extends ClusterClient implements Runnable {
  private final TokenClientConfig clientConfig;
  private volatile Object mutexDoNotUseDirectly;
  private String currentServer;
  private final Stopwatch stopwatch;
  private final AtomicInteger counter = new AtomicInteger();
  private volatile boolean flag;

  public TokenClient(TokenClientConfig clientConfig) {
    super(clientConfig);
    this.clientConfig = clientConfig;
    this.stopwatch = Stopwatch.createStarted();
  }

  private static void acquire(int count, String address) {
    // todo..
  }

  @Override
  public void run() {
    synchronized (this.mutex()) {
      while (true) { // todo... running check
        doCheckServer();
        // 本地无可用permit或者超期时才拉取permit
        var remained = counter.get();
        var outdate = isOutdate();
        if (remained <= 0 || outdate) {
          // wait until acquire enough permits
          acquire(
              this.clientConfig.getPermitsPerRequest() - (outdate ? 0 : remained),
              this.currentServer);
          this.counter.set(this.clientConfig.getPermitsPerRequest());
          this.stopwatch.reset().start();
        }
        flag = true;
        try {
          this.mutex().wait(500L);
        } catch (InterruptedException e) {
          // ignore
        }
      }
    }
  }

  @Override
  protected void doCheckServer() {
    super.doCheckServer();
    this.currentServer = findServer(this.clientConfig.getKey());
  }

  private boolean isOutdate() {
    return stopwatch.elapsed(TimeUnit.MICROSECONDS) >= 1_000_000L;
  }

  public boolean canAcquire(int permits) { // to fail-fast don't use permits
    return flag && counter.get() > 0 && !isOutdate();
  }

  public boolean tryAcquire(int permits) {
    if (canAcquire(permits)) {
      synchronized (this.mutex()) {
        var canAcquire = canAcquire(permits);
        // 本地允许扣减成负数，不能减成负数而是reject不合理，或者wait也不合理；缺点是极端情况下突发流量会很高。
        if (!(flag = canAcquire && counter.addAndGet(-permits) > 0)) {
          this.mutex().notify();
        }
        return canAcquire;
      }
    }
    return false;
  }

  // 使用private的对象锁避免被外部抢占
  private Object mutex() {
    Object mutex = this.mutexDoNotUseDirectly;
    if (mutex == null) {
      synchronized (this) {
        mutex = this.mutexDoNotUseDirectly;
        if (mutex == null) {
          this.mutexDoNotUseDirectly = mutex = new Object();
        }
      }
    }
    return mutex;
  }
}
