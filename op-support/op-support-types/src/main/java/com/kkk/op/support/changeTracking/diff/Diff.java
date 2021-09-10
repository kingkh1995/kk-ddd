package com.kkk.op.support.changeTracking.diff;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * 对于原始类型无ObjectDiff子类，意图在于节约内存
 *
 * @author KaiKoo
 */
@Data
public abstract class Diff {

  @Setter(AccessLevel.PACKAGE)
  private Object oldValue;

  @Setter(AccessLevel.PACKAGE)
  private Object newValue;

  @Setter(AccessLevel.PACKAGE)
  private DiffType type;

  Diff(Object oldValue, Object newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }
}
