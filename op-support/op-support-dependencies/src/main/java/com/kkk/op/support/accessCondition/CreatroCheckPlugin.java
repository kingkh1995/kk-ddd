package com.kkk.op.support.accessCondition;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class CreatroCheckPlugin implements AccessConditionCheckPlugin {

  @Override
  public AccessConditionPluginEnum getStrategyID() {
    return AccessConditionPluginEnum.creator;
  }
}
