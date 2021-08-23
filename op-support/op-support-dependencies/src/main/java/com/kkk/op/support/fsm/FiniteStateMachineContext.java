package com.kkk.op.support.fsm;

/**
 * 状态机上下文对象<br>
 *
 * @author KaiKoo
 */
import com.kkk.op.support.base.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class FiniteStateMachineContext<
    E extends FiniteStateMachineEvent, T extends Entity> {

  @Getter private E event;

  @Getter private T entity;

  abstract String getState();

  abstract String getBiz();

  abstract String getScene();
}
