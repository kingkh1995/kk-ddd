package com.kkk.op.support.util.strategy;

/**
 * 管道模式处理器marker <br>
 *
 * @author KaiKoo
 */
public interface PipelineHandler<C, K> extends Strategy<K> {

  boolean handle(C context);
}
