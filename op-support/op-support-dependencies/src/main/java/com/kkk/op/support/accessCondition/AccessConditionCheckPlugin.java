package com.kkk.op.support.accessCondition;

import com.kkk.op.support.marker.Strategy;

/**
 * AccessCondition插件接口
 *
 * @author KaiKoo
 */
public interface AccessConditionCheckPlugin extends Strategy<AccessConditionPluginEnum> {

  boolean canAccess(Object obj, String args);
}
