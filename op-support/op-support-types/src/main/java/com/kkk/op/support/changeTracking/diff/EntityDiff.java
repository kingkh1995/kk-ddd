package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.marker.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * TBD... fieldName参数修改为DP
 * 实体类的对比信息（可以实现Map接口）
 *
 * @author KaiKoo
 */
public class EntityDiff extends Diff {

    /**
     * 一个 Aggregate 由 Types（DP和其他基本数据类型） 和 Entity 和 Collection 组成
     * 而一个 Entity 应该只由 Types 组成，同时一个 Aggregate 的主数据就等同于一个 Entity ，所以也应该只由 Types 组成
     * Modify状态下，如果所有Types均未被更新，则表示Aggregate主数据未被更新，将selfModified设为false，更新时则不更新主数据
     * Add Remove 类型下 selfModified 必然为true
     * TBD... 优化判断逻辑 利用注解标识主数据的字段
     */
    @Getter
    @Setter
    private boolean selfModified = false; //默认为false

    /**
     * 多数情况下map可能为空，为节约内存，在put时才去创建一个 HashMap
     */
    protected Map<String, Diff> map = Collections.EMPTY_MAP;

    public EntityDiff(Entity oldValue, Entity newValue) {
        super(oldValue, newValue);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(String fieldName) {
        return this.map.containsKey(fieldName);
    }

    public Diff get(String fieldName) {
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

    @Override
    public void setType(DiffType type) {
        super.setType(type);
        // Add Remove 类型下 selfModified 设为 true
        if (type == DiffType.Added || type == DiffType.Removed) {
            this.setSelfModified(true);
        }
    }

    /**
     * 参考 Collections.EMPTY_MAP 设计
     */
    @Deprecated
    public final static EntityDiff EMPTY = new EmptyEntityDiff();

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
