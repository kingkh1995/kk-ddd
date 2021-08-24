package com.kkk.op.support.fsm;

/**
 * 状态流转事件接口 <br>
 *
 * @author KaiKoo
 */
public interface FsmEvent {

  /** 事件ID */
  String getEventId();

  /** 实体ID */
  String getEntityId();

  /** 是否要新创建数据 */
  boolean newCreate();
}
