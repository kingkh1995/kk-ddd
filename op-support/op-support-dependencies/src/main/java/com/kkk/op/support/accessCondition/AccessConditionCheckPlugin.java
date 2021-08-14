package com.kkk.op.support.accessCondition;

import com.kkk.op.support.marker.Strategy;
import java.util.concurrent.ThreadLocalRandom;

/**
 * AccessCondition插件接口
 *
 * @author KaiKoo
 */
public interface AccessConditionCheckPlugin extends Strategy<AccessConditionPluginEnum> {

  default boolean canAcess(String args) {
    // fixme... 暂时Mock住
    return ThreadLocalRandom.current().nextBoolean();
  }
}
