package com.kkk.op.support.changeTracking.diff;

import lombok.Data;

/**
 * 对于原始类型无ObjectDiff子类，意图在于节约内存
 *
 * @author KaiKoo
 */
@Data
public abstract class Diff {

  private Object oldValue;

  private Object newValue;

  private DiffType type;

  public Diff(Object oldValue, Object newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }
}
