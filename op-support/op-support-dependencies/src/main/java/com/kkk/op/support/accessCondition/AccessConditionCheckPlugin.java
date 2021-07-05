package com.kkk.op.support.accessCondition;

/**
 * 方法访问条件检查插件接口
 *
 * @author KaiKoo
 */
public interface AccessConditionCheckPlugin {

  String name();

  // 默认不校验
  default boolean canAccess() {
    return true;
  }
}
