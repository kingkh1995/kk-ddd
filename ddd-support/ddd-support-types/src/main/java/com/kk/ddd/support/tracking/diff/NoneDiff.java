package com.kk.ddd.support.tracking.diff;

/**
 * <br>
 *
 * @author KaiKoo
 */
public final class NoneDiff extends Diff {
  static final NoneDiff INSTANCE = new NoneDiff();

  private NoneDiff() {
    super(null, null);
  }

  @Override
  public DiffType getDiffType() {
    return DiffType.None;
  }

  @Override
  public ChangeType getChangeType() {
    return null;
  }

  @Override
  public boolean isSelfModified() {
    return false;
  }

  @Override
  public boolean isNoneDiff() {
    return true;
  }
}
