package com.kk.ddd.support.util.task;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface Container<C> {
  TaskResult execute(C context);
}
