package com.kk.ddd.support.util.strategy;

/**
 * 管道模式处理器marker <br>
 *
 * @author KaiKoo
 */
public interface PipelineHandler<C> {

  int order();

  boolean handle(C context);
}
