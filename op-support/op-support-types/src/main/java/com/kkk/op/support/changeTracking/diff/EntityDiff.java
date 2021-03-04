package com.kkk.op.support.changeTracking.diff;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * 实体类的对比信息（可以实现Map接口）
 * @author KaiKoo
 */
public class EntityDiff extends Diff {

    @Getter
    @Setter
    private Object oldValue;

    @Getter
    @Setter
    private Object newValue;

    /**
     * todo... 优化判断逻辑 利用注解标识主数据的字段
     * Aggregate由 Type 和 Entity 组成，Type组成其主数据
     * 如果所有Type均未被更新，则表示主数据未被更新，selfModified为false
     */
    @Getter
    @Setter
    private boolean selfModified;

    private final Map<String, Diff> map;

    public final static EntityDiff EMPTY = new EntityDiff(false, Collections.EMPTY_MAP);

    public final static EntityDiff WRONG = new EntityDiff(true, Collections.EMPTY_MAP);

    private EntityDiff(boolean selfModified, Map<String, Diff> map) {
        this.selfModified = selfModified;
        this.map = map;
    }

    public EntityDiff(Object oldValue, Object newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.map = new HashMap<>();
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(String fieldName) {
        return this.map.containsKey(fieldName);
    }

    public Object get(String fieldName) {
        return this.map.get(fieldName);
    }

    public Object put(String fieldName, Diff diff) {
        return this.map.put(fieldName, diff);
    }

}
