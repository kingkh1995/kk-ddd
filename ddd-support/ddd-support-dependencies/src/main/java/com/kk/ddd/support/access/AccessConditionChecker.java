package com.kk.ddd.support.access;

import com.kk.ddd.support.aspect.QueryServiceChecker;

/**
 * <br>
 *
 * @author kingk
 */
public interface AccessConditionChecker extends QueryServiceChecker {
  @Override
  default boolean checkBefore(Object target, Object[] args) {
    var accessCondition = AccessConditionHelper.get();
    if (accessCondition == null) {
      return true;
    }
    return checkAccessConditionBefore(accessCondition, target, args);
  }

  @Override
  default boolean checkAfter(Object target, Object Result) {
    var accessCondition = AccessConditionHelper.get();
    if (accessCondition == null) {
      return true;
    }
    return checkAccessConditionAfter(accessCondition, target, Result);
  }

  boolean checkAccessConditionBefore(String accessCondition, Object target, Object[] args);

  boolean checkAccessConditionAfter(String accessCondition, Object target, Object Result);
}
