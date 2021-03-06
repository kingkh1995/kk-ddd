package com.kkk.op.support.changeTracking.diff;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * TBD... fieldName要不要改为Type类型DP
 * 实体类的对比信息（可以实现Map接口）
 * @author KaiKoo
 */
public class EntityDiff extends Diff {

    /**
     * 一个 Aggregate 由 Types 和 Entity 和 Collection 组成，Type组成其主数据
     * Modify状态下，如果所有Types均未被更新，则表示主数据未被更新，selfModified设为false，更新时则不更新主数据
     * Add Remove类型 selfModified 必然为true
     * TBD... 优化判断逻辑 利用注解标识主数据的字段
     */
    @Getter
    @Setter
    private boolean selfModified = false;

    /**
     * 多数情况下map可能为空，为节约内存，在put时才去创建一个 HashMap
     */
    protected Map<String, Diff> map = Collections.EMPTY_MAP;

    /**
     * 参考 Collections.EMPTY_MAP 设计
     */
    public final static EntityDiff EMPTY = new EmptyEntityDiff();

    public EntityDiff(Object oldValue, Object newValue) {
        super(oldValue, newValue);
    }

    public int size() {
        return this.map.size();
    }

    /**
     * 组合判断
     */
    @Override
    public boolean isEmpty() {
        return this.isMapEmpty() && !this.isSelfModified();
    }

    /**
     * map是否为空
     */
    public boolean isMapEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(String fieldName) {
        return this.map.containsKey(fieldName);
    }

    public Object get(String fieldName) {
        return this.map.get(fieldName);
    }

    public Diff put(String fieldName, Diff diff) {
        if (diff == null) {
            return null;
        }
        if (this.isEmpty()) {
            this.map = new HashMap<>();
        }
        return this.map.put(fieldName, diff);
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    /**
     * 私有内部类
     */
    private static class EmptyEntityDiff extends EntityDiff {

        public EmptyEntityDiff() {
            super(null, null);
        }

        @Override
        public Diff put(String fieldName, Diff diff) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSelfModified() {
            return false;
        }

        @Override
        public void setSelfModified(boolean selfModified) {
            throw new UnsupportedOperationException();
        }
    }

}
