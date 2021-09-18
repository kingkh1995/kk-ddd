package com.kkk.op.support.access;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class CreatorCheckPlugin implements AccessConditionCheckPlugin {

  @Override
  public AccessConditionPluginEnum getIdentifier() {
    return AccessConditionPluginEnum.creator;
  }

  @Override
  public boolean canAccess(Object obj, String args) {
    // todo... 待实现
    return false;
  }
}
