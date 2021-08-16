package com.kkk.op.support.accessCondition;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class CreatorCheckPlugin implements AccessConditionCheckPlugin {

  @Override
  public AccessConditionPluginEnum getStrategyID() {
    return AccessConditionPluginEnum.creator;
  }

  @Override
  public boolean canAcess(Object obj, String args) {
    // todo... 待实现
    return false;
  }
}
