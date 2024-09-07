package com.kk.ddd.support.util.task;

/**
 * <br>
 *
 * @author kingk
 */
public abstract class TaskContext {
  private volatile boolean failed = false;

  public final boolean isFailed() {
    return failed;
  }

  public void failFast() {
    failed = true;
  }
}
