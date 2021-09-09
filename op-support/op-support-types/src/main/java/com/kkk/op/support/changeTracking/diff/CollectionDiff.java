package com.kkk.op.support.changeTracking.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 集合类对比信息（可以实现Collection接口）<br>
 * type默认为Modified
 *
 * @author KaiKoo
 */
public class CollectionDiff extends Diff {

  /** 多数情况下list可能为空，为节约内存，在add时才去创建一个 ArrayList */
  private List<Diff> list = Collections.EMPTY_LIST;

  public CollectionDiff(Collection<?> oldValue, Collection<?> newValue) {
    super(oldValue, newValue);
  }

  // type默认为Modified
  @Override
  public DiffType getType() {
    return DiffType.Modified;
  }

  // do nothing
  @Override
  public void setType(DiffType type) {
    return;
  }

  public boolean isEmpty() {
    return this.list.isEmpty();
  }

  public boolean add(Diff diff) {
    if (diff == null) {
      return false;
    }
    if (this.list.isEmpty()) {
      this.list = new ArrayList<>();
    }
    return this.list.add(diff);
  }

  public Iterator<Diff> iterator() {
    return this.list.iterator();
  }
}
