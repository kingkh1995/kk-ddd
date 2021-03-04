package com.kkk.op.support.changeTracking.diff;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合类对比信息（可以实现Collection接口）
 * @author KaiKoo
 */
public class CollectionDiff extends Diff {

    private final List<Diff> list = new ArrayList<>();

    public int size() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public boolean add(Diff diff) {
        return this.list.add(diff);
    }

    public boolean addAll(List<Diff> diffs) {
        return this.addAll(diffs);
    }
}
