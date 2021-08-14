package com.kkk.op.support.accessCondition;

/**
 * AccessCondition检测插件枚举 <br>
 *
 * @author KaiKoo
 */
public enum AccessConditionPluginEnum {
  creator, // 操作人员必须是数据创建者
  permission, // 必须拥有指定权限
  source, // 必须是指定请求来源
  ;
}
