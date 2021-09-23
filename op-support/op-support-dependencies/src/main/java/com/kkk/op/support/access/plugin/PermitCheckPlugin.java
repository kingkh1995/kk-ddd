package com.kkk.op.support.access.plugin;

import com.kkk.op.support.access.AccessConditionCheckException;
import com.kkk.op.support.access.AccessConditionCheckPlugin;

/**
 * 必须拥有指定权限 <br>
 *
 * @author KaiKoo
 */
public class PermitCheckPlugin implements AccessConditionCheckPlugin {

  @Override
  public String getIdentifier() {
    return "permit";
  }

  @Override
  public boolean canAccess(Object obj, String args) {
    if (args == null || args.isBlank()) {
      throw new AccessConditionCheckException("PermitCheckPlugin, args shouldn't be blank");
    }
    var perms = args.split(",");
    // todo... get permits from LocalRequestContext then compare
    return false;
  }
}
