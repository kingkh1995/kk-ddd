package com.kkk.op.support.changeTracking.diff;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import lombok.Getter;

/**
 * 基类（部分参考JsonNode设计）
 *
 * @author KaiKoo
 */
public abstract class Diff {

  @Getter private Object oldValue;

  @Getter private Object newValue;

  Diff(Object oldValue, Object newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public abstract DiffType getDiffType();

  public ChangeType getChangeType() {
    return null;
  }

  public Iterator<String> fieldNames() {
    return Collections.emptyIterator();
  }

  public Iterator<Entry<String, Diff>> fields() {
    return Collections.emptyIterator();
  }

  public Iterator<Diff> elements() {
    return Collections.emptyIterator();
  }

  public int size() {
    return 0;
  }

  public boolean isEmpty() {
    return this.size() == 0;
  }

  public boolean isCollectionDiff() {
    return DiffType.Collection == this.getDiffType();
  }

  public boolean isEntityDiff() {
    return DiffType.Entity == this.getDiffType();
  }

  public boolean isValueDiff() {
    return DiffType.Value == this.getDiffType();
  }
}
