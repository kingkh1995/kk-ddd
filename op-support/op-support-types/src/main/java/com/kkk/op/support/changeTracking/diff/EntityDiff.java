package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.base.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.Getter;

/**
 * 实体类的对比信息（可以考虑实现Map接口）
 *
 * @author KaiKoo
 */
public class EntityDiff extends Diff {

  EntityDiff(Entity<?> oldValue, Entity<?> newValue) {
    super(oldValue, newValue);
  }

  /** 多数情况下map可能为空，为节约内存，在put时才去创建一个 HashMap */
  protected Map<String, Diff> map = Collections.EMPTY_MAP;

  /**
   * 一个 Aggregate 应该只由 Types（DP和其他基本数据类型）、 Entity 、 Collection 组成 <br>
   * 而一个 Entity 应该只由 Types 组成，所以一个 Aggregate 所有的 Types 组成其主数据，等同于一个 Entity <br>
   * Modify状态下，如果所有Types均未被更新，则表示Aggregate主数据未被更新，将selfModified设为false，更新时则不更新主数据 <br>
   * Add Remove 类型下 selfModified 必然为true <br>
   * todo... TBD 优化判断逻辑，利用注解标识Aggregate主数据的字段
   */
  @Getter private boolean selfModified;

  @Getter private ChangeType changeType;

  @Override
  public DiffType getDiffType() {
    return DiffType.Entity;
  }

  void setSelfModified(boolean selfModified) {
    // 仅Modify类型下才允许设置selfModified
    if (this.changeType == ChangeType.Modified) {
      this.selfModified = selfModified;
    }
  }

  void setChangeType(ChangeType changeType) {
    if (changeType != null) {
      return;
    }
    if (changeType == ChangeType.Modified) {
      this.selfModified = false;
    } else {
      this.selfModified = true;
    }
    this.changeType = changeType;
  }

  public Optional<Diff> get(String fieldName) {
    return Optional.ofNullable(this.map.get(fieldName));
  }

  public <T extends Entity<?>, R> Optional<Diff> lambdaGet(SFunction<T, R> func) {
    return this.get(funcToProperty(func));
  }

  static String funcToProperty(SFunction<?, ?> func) {
    return methodToProperty(LambdaHelper.resolve(func).getImplMethodName());
  }

  static String methodToProperty(String method) {
    if (method.startsWith("is")) {
      method = method.substring(2);
    } else if (method.startsWith("get")) {
      method = method.substring(3);
    } else {
      throw new IllegalArgumentException(
          "Error parsing property from method '"
              + method
              + "' for didn't start with 'is' or 'get'!");
    }
    if (method.length() > 0) {
      method = method.substring(0, 1).toLowerCase() + method.substring(1);
    }
    return method;
  }

  boolean put(String fieldName, Diff diff) {
    if (diff == null) {
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

  /** 参考 Collections.EMPTY_MAP 设计 */
  @Deprecated public static final EntityDiff EMPTY = new EmptyEntityDiff();

  /** 私有内部类 */
  private static class EmptyEntityDiff extends EntityDiff {

    EmptyEntityDiff() {
      super(null, null);
    }

    @Override
    boolean put(String fieldName, Diff diff) {
      throw new UnsupportedOperationException();
    }

    @Override
    void setSelfModified(boolean selfModified) {
      throw new UnsupportedOperationException();
    }

    public boolean isSelfModified() {
      return false;
    }
  }
}
