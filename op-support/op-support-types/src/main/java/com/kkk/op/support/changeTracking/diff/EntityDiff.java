package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.base.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 实体类的对比信息（可以考虑实现Map接口）
 *
 * @author KaiKoo
 */
public final class EntityDiff extends Diff {

  EntityDiff(Entity<?> oldValue, Entity<?> newValue) {
    super(oldValue, newValue);
  }

  /** 多数情况下map可能为空，为节约内存，在put时才去创建一个 HashMap */
  private Map<String, Diff> map = Collections.emptyMap();

  /**
   * 一个 Aggregate 应该只由 Types（DP和其他基本数据类型）、 Entity 、 Collection 组成 <br>
   * 而一个 Entity 应该只由 Types 组成，所以一个 Aggregate 所有的 Types 组成其主数据，等同于一个 Entity <br>
   * Modify状态下，如果所有Types均未被更新，则表示Aggregate主数据未被更新，将selfModified设为false，更新时则不更新主数据 <br>
   * Add Remove 类型下 selfModified 必然为true <br>
   */
  @Getter private boolean selfModified;

  @Getter private ChangeType changeType;

  @Override
  public DiffType getDiffType() {
    return DiffType.Entity;
  }

  @Override
  public boolean isEntityDiff() {
    return true;
  }

  EntityDiff setSelfModified(boolean selfModified) {
    // 仅Modify类型下才允许设置selfModified
    if (this.changeType == ChangeType.Modified) {
      this.selfModified = selfModified;
    }
    return this;
  }

  EntityDiff setChangeType(ChangeType changeType) {
    if (this.changeType != null || changeType == null) {
      return this;
    }
    this.selfModified = changeType != ChangeType.Modified;
    this.changeType = changeType;
    return this;
  }

  @Override
  public Diff get(String fieldName) {
    return Optional.ofNullable(this.map.get(fieldName)).orElse(NoneDiff.INSTANCE);
  }

  boolean put(String fieldName, @NotNull Diff diff) {
    if (diff.isNoneDiff()) {
      return false;
    }
    if (this.map.isEmpty()) {
      this.map = new HashMap<>();
    }
    this.map.put(fieldName, diff);
    return true;
  }

  @Override
  public int size() {
    return this.map.size();
  }

  @Override
  public Iterator<String> fieldNames() {
    return this.map.keySet().iterator();
  }

  @Override
  public Iterator<Entry<String, Diff>> fields() {
    return this.map.entrySet().iterator();
  }

  @Override
  public Iterator<Diff> elements() {
    return this.map.values().iterator();
  }
}
