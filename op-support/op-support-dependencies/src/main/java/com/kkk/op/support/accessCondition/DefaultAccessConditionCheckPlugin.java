package com.kkk.op.support.accessCondition;

import com.kkk.op.support.annotations.AccessCondition;

/**
 * 默认插件
 *
 * @author KaiKoo
 */
public class DefaultAccessConditionCheckPlugin implements AccessConditionCheckPlugin {

  @Override
  public String name() {
    return AccessCondition.DEFUALT;
  }

}
