package com.kkk.op.support.changeTracking.diff;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class ValueDiff extends Diff {

  ValueDiff(Object oldValue, Object newValue) {
    super(oldValue, newValue);
  }

  @Override
  public DiffType getDiffType() {
    return DiffType.Value;
  }

  @Override
  public ChangeType getChangeType() {
    return ChangeType.Modified;
  }
}
