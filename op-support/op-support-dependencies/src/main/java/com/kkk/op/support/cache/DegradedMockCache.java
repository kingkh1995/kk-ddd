package com.kkk.op.support.cache;

import com.kkk.op.support.marker.EntityCache.ValueWrapper;
import java.util.Optional;

/**
 * MockCache降级回调工具类 <br>
 * 需要全为静态方法，且方法名参数一致，可以只定义部分方法。
 *
 * @see MockCache
 * @author KaiKoo
 */
public class DegradedMockCache {

  public static <T> Optional<ValueWrapper<T>> get(String key, Class<T> clazz) {
    return Optional.empty();
  }

  public static void put(String key, Object obj) {}

  public static void evict(String key) {}

  public static void clear() {}
}
