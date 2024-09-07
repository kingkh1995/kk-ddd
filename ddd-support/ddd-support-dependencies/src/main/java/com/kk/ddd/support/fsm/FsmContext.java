package com.kk.ddd.support.fsm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 状态机上下文对象 <br>
 *
 * @author KaiKoo
 */
@RequiredArgsConstructor
public abstract class FsmContext<E extends FsmEvent, T> {

  @Getter private final E event;

  @Getter private final T entity;

  public abstract String getState();

  public abstract String getBiz();

  public abstract String getScene();
}
