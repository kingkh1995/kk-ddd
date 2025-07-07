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

  private static int acquire(int count, String address) {
    // todo..
    return 0;
  }

  @Override
  public void run() {
    synchronized (this.mutex()) {
      while (true) { // todo... running check
        doCheckServer();
        while (counter.get() <= 0
            || stopwatch.elapsed(TimeUnit.MILLISECONDS) >= 1_000L) { // overtake
          if (counter.get() > 0) { // overdate
            this.counter.set(0);
          }
          this.stopwatch.reset().start();
          var acquired = acquire(this.clientConfig.getCountPerRequest(), this.currentServer);
          this.counter.addAndGet(acquired);
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

  public boolean canAcquire(int permits) { // to fail-fast don't use permits
    return flag && counter.get() > 0 && stopwatch.elapsed(TimeUnit.MILLISECONDS) < 1_000L;
  }

  public boolean tryAcquire(int permits) {
    if (canAcquire(permits)) {
      synchronized (this.mutex()) {
        var canAcquire = canAcquire(permits);
        if (!(flag = canAcquire && counter.addAndGet(-permits) > 0)) {
          this.mutex().notify();
        }
        return canAcquire;
      }
    }
    return false;
  }

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
