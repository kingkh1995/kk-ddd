package com.kk.ddd.support.util;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Class工具类 <br>
 *
 * @author KaiKoo
 */
public final class ClassUtils {

  private ClassUtils() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  public static Object getDefault(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    // 基本数据类型（如：int.class）
    if (clazz.isPrimitive()) {
      // 使用switch表达式
      return switch (clazz.getName()){
        case "void" -> null;
        case "boolean" -> false;
        case "char" -> '\0';
        default -> 0;
      };
    }
    //  数组类型
    if (clazz.isArray()) {
      return Array.newInstance(clazz.getComponentType(), 0);
    }
    // 集合类型
    if (List.class.isAssignableFrom(clazz)) {
      return Collections.emptyList();
    }
    if (Set.class.isAssignableFrom(clazz)) {
      return Collections.emptySet();
    }
    if (Map.class.isAssignableFrom(clazz)) {
      return Collections.emptyMap();
    }
    if (Iterator.class.isAssignableFrom(clazz)) {
      return Collections.emptyIterator();
    }
    if (Optional.class.isAssignableFrom(clazz)) {
      return Optional.empty();
    }
    // 其他情况返回null
    return null;
  }
}
