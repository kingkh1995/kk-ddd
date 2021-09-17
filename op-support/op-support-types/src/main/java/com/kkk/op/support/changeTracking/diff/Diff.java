package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.base.Entity;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import lombok.Getter;

/**
 * 基类（部分参考JsonNode设计）定义为密封类
 *
 * @author KaiKoo
 */
public abstract sealed class Diff permits EntityDiff, CollectionDiff, NoneDiff{

  @Getter private Object oldValue;

  @Getter private Object newValue;

  Diff(Object oldValue, Object newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public abstract DiffType getDiffType();

  public abstract ChangeType getChangeType();

  public boolean isSelfModified() {
    return true;
  }

  public boolean isEntityDiff() {
    return false;
  }

  public boolean isCollectionDiff() {
    return false;
  }

  public boolean isNoneDiff() {
    return false;
  }

  public Diff get(String fieldName) {
    return NoneDiff.INSTANCE;
  }

  public <T extends Entity<?>, R> Diff lambdaGet(SFunction<T, R> func) {
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

  public Diff get(int index) {
    return NoneDiff.INSTANCE;
  }

  public int size() {
    return 0;
  }

  public boolean isEmpty() {
    return this.size() == 0;
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
}
