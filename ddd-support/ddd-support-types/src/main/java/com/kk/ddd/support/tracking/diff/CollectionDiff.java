package com.kk.ddd.support.tracking.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * 集合类对比信息（可以考虑实现Collection接口）<br>
 *
 * @author KaiKoo
 */
public final class CollectionDiff extends Diff {

  CollectionDiff(Collection<?> oldValue, Collection<?> newValue) {
    super(oldValue, newValue);
  }

  /** 多数情况下list可能为空，为节约内存，在add时才去创建一个 ArrayList */
  private List<Diff> list = Collections.emptyList();

  @Override
  public DiffType getDiffType() {
    return DiffType.Collection;
  }

  @Override
  public ChangeType getChangeType() {
    return ChangeType.Modified;
  }

  @Override
  public boolean isCollectionDiff() {
    return true;
  }

  @Override
  public Diff get(int index) {
    if (index >= 0 && index < this.list.size()) {
      return this.list.get(index);
    }
    return NoneDiff.INSTANCE;
  }

  boolean add(@NotNull Diff diff) {
    if (diff.isNoneDiff()) {
      return false;
    }
    if (this.list.isEmpty()) {
      this.list = new ArrayList<>();
    }
    return this.list.add(diff);
  }

  @Override
  public int size() {
    return this.list.size();
  }

  @Override
  public Iterator<Diff> elements() {
    return this.list.iterator();
  }
}
