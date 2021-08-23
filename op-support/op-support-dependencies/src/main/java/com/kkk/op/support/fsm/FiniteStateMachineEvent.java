package com.kkk.op.support.fsm;

/**
 * 状态流转事件接口 <br>
 *
 * @author KaiKoo
 */
public interface FiniteStateMachineEvent {

  /** 状态事件ID */
  String getEventId();

  /** 实体ID */
  String getEntityId();

  /** 如果matchingState不为空，则代表只有实体是指定的状态才允许触发事件 */
  default String matchingState() {
    return null;
  }

  /** 判断实体当前状态是否与事件指定状态匹配 */
  default boolean matches(String currentState) {
    return this.matchingState() == null ? true : this.matchingState().equals(currentState);
  }

  /** 是否要新创建数据 */
  boolean newCreate();
}
