package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;

/**
 * FiniteStateMachine 状态机引擎接口 <br>
 *
 * @author KaiKoo
 */
public interface FsmEngine<E extends FsmEvent, T extends Entity> {

  /** 执行状态迁移事件，不传entity默认会根据entityId获取 */
  void sendEvent(E event) throws Exception;

  /** 执行状态迁移事件，可携带entity参数（主要是新增事件，或者避免重复查询而将上游查询结果传入） */
  void sendEvent(E event, T entity) throws Exception;
}
