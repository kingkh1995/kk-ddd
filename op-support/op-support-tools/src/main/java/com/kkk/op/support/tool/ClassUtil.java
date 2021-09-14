package com.kkk.op.support.tool;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class工具类 <br>
 *
 * @author KaiKoo
 */
public final class ClassUtil {

  private ClassUtil() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  public static Object getDefault(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    // 基本数据类型（如：int.class）
    if (clazz.isPrimitive()) {
      switch (clazz.getName()) {
        case "void":
          return null;
        case "boolean":
          return false;
        case "char":
          return '\0';
        default:
          return 0;
      }
    }
    //  数组类型
    if (clazz.isArray()) {
      return Array.newInstance(clazz.getComponentType(), 0);
    }
    // 集合类型，使用isAssignableFrom判断当前类是不是给定类或其父类，因为方法返回值类型一般都只选择父类所以如此判断。
    if (clazz.isAssignableFrom(List.class)) {
      return Collections.emptyList();
    } else if (clazz.isAssignableFrom(Set.class)) {
      return Collections.emptySet();
    } else if (clazz.isAssignableFrom(Map.class)) {
      return Collections.emptyMap();
    } else if (clazz.isAssignableFrom(Iterator.class)) {
      return Collections.emptyIterator();
    }
    // 其他情况返回null
    return null;
  }

}
