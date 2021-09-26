package com.kkk.op.support.access.plugin;

import com.kkk.op.support.access.AccessConditionCheckException;
import com.kkk.op.support.access.AccessConditionCheckPlugin;
import com.kkk.op.support.base.LocalRequestContextHolder;
import java.util.Objects;

/**
 * 操作人员必须是数据创建者 <br>
 *
 * @author KaiKoo
 */
public class CreatorCheckPlugin implements AccessConditionCheckPlugin {

  @Override
  public String getIdentifier() {
    return "creator";
  }

  @Override
  public boolean canAccess(Object obj, String args) {
    return AccessConditionCheckPlugin.deepCheck(
        obj,
        o -> {
          try {
            var field = o.getClass().getDeclaredField("creator");
            field.trySetAccessible();
            return Objects.equals(field.get(o), LocalRequestContextHolder.get().getOperatorId());
          } catch (Throwable e) {
            throw new AccessConditionCheckException(e);
          }
        });
  }
}