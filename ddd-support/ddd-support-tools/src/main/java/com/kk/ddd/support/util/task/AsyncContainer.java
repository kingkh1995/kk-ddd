package com.kk.ddd.support.util.task;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface AsyncContainer<C> extends Container<C> {
  TaskResult execute(C context, int timeout);
}
