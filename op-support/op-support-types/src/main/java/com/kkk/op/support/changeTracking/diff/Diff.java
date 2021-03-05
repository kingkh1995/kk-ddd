package com.kkk.op.support.changeTracking.diff;

import lombok.Data;

/**
 * 无 ObjectDiff 子类，意图在于减少内存消耗
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
