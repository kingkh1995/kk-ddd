package com.kkk.op.support.tools;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 反射工具类 <br>
 *
 * @author KaiKoo
 */
public final class ReflectUtil {

  private ReflectUtil() throws IllegalAccessException {
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
    // 集合类：因为返回值类型都会选择父类，但是返回值会是子类，所以使用isAssignableFrom判断无误。
    if (clazz.isAssignableFrom(List.class)) {
      return Collections.EMPTY_LIST;
    } else if (clazz.isAssignableFrom(Set.class)) {
      return Collections.EMPTY_SET;
    } else if (clazz.isAssignableFrom(Map.class)) {
      return Collections.EMPTY_MAP;
    }
    // 其他情况返回null
    return null;
  }
}
