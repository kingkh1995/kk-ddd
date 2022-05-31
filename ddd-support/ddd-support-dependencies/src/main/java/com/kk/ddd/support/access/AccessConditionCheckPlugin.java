package com.kk.ddd.support.access;

import com.kk.ddd.support.util.strategy.Strategy;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * AccessCondition插件接口
 *
 * @author KaiKoo
 */
public interface AccessConditionCheckPlugin extends Strategy<String> {

  boolean canAccess(Object obj, String args);

  static boolean deepCheck(Object obj, Predicate<Object> predicate) {
    if (obj instanceof Iterable iterable) {
      return checkIterator(iterable.iterator(), predicate);
    } else if (obj instanceof Map map) {
      return checkIterator(map.values().iterator(), predicate);
    }
    return checkObject(obj, predicate);
  }

  static boolean checkIterator(Iterator<?> it, Predicate<Object> predicate) {
    while (it.hasNext()) {
      if (!checkObject(it.next(), predicate)) {
        return false;
      }
    }
    return true;
  }

  // if null return true
  static boolean checkObject(Object o, Predicate<Object> predicate) {
    return Objects.isNull(o) || predicate.test(o);
  }
}
