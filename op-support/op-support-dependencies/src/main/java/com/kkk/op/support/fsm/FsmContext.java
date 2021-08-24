package com.kkk.op.support.fsm;

/**
 * 状态机上下文对象 <br>
 *
 * @author KaiKoo
 */
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class FsmContext<E extends FsmEvent, T> {

  @Getter private E event;

  @Getter private T entity;

  abstract String getState();

  abstract String getBiz();

  abstract String getScene();
}
