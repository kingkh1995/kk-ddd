package com.kk.ddd.support.util.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface AsyncContainer<C> extends Container<C> {
  TaskResult execute(C context, int timeout)
      throws ExecutionException, InterruptedException, TimeoutException;
}
