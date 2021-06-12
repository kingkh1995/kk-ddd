package com.kkk.op.support.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 查找工具类
 *
 * @author KaiKoo
 */
public final class SearchUtil {

  private SearchUtil() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final List<Integer> FIBSEQ =
      new CopyOnWriteArrayList<>(new Integer[] {1, 1, 2, 3, 5, 8, 13, 21, 34, 55});

  // 返回大于等于 length 的第一个斐波那契数的索引
  private static int getFibIndex(int length) {
    if (length < 2) {
      throw new IllegalArgumentException();
    }
    if (length > FIBSEQ.get(FIBSEQ.size() - 1)) {
      fibGrow(length);
    }
    // 二分查找
    var lo = 0;
    var hi = FIBSEQ.size() - 1;
    while (lo <= hi) {
      var mid = (hi + lo) >>> 1;
      if (FIBSEQ.get(mid) >= length) {
        if (FIBSEQ.get(mid - 1) < length) {
          return mid;
        } else {
          hi = mid - 1;
        }
      } else {
        if (FIBSEQ.get(mid + 1) >= length) {
          return mid + 1;
        } else {
          lo = mid + 1;
        }
      }
    }
    return hi;
  }

  private static synchronized void fibGrow(int length) {
    // 再次判断
    var i = FIBSEQ.size() - 1;
    if (length > FIBSEQ.get(i)) {
      var tempList = new ArrayList<Integer>();
      var n = FIBSEQ.get(i - 1) + FIBSEQ.get(i);
      tempList.add(n);
      var j = 0;
      n = FIBSEQ.get(i) + tempList.get(j++);
      tempList.add(n);
      while (n < length) {
        n = tempList.get(j - 1) + tempList.get(j++);
        tempList.add(n);
      }
      FIBSEQ.addAll(tempList);
    }
  }

  /** 斐波那契查找 效率与二分查找一致 对磁盘比较友好 */
  public static int fibSearch(int[] arr, int key) {
    var length = arr.length;
    if (length == 1) {
      return arr[0] == key ? 0 : -1;
    }
    var k = getFibIndex(length) - 1;
    var lo = 0;
    var hi = length - 1;
    while (lo <= hi) {
      var mid = lo + FIBSEQ.get(k) - 1;
      if (safeGet(arr, mid) < key) {
        k = k - 1;
        lo = mid + 1;
      } else if (safeGet(arr, mid) > key) {
        k = k - 2;
        hi = mid - 1;
      } else {
        // 等于情况还要额外判断一次
        return mid >= length ? length - 1 : mid;
      }
    }
    // 不存在时返回
    return -(lo + 1);
  }

  // 找出大于等于key的最小值
  public static OptionalInt ceil(int[] arr, int key) {
    var i = fibSearch(arr, key);
    // key存在
    if (i >= 0) {
      return OptionalInt.of(key);
    }
    // 因为如果查找不到返回-(lo + 1) 求出lo的值
    i = -i - 1;
    if (i < arr.length) {
      return OptionalInt.of(arr[i]);
    }
    return OptionalInt.empty();
  }

  // 找出小于等于key的最大值
  public static OptionalInt floor(int[] arr, int key) {
    var i = fibSearch(arr, key);
    // key存在
    if (i >= 0) {
      return OptionalInt.of(key);
    }
    // 因为如果查找不到返回-(lo + 1) 求出lo-1的值
    i = -i - 2;
    if (i >= 0) {
      return OptionalInt.of(safeGet(arr, i));
    }
    return OptionalInt.empty();
  }

  // 安全获取数组元素，超出范围取两端
  private static int safeGet(int[] arr, int index) {
    if (index >= arr.length) {
      return arr[arr.length - 1];
    } else if (index >= 0) {
      return arr[index];
    } else {
      return arr[0];
    }
  }

  public static int fibSearch(long[] arr, long key) {
    var length = arr.length;
    if (length == 1) {
      return arr[0] == key ? 0 : -1;
    }
    var k = getFibIndex(length) - 1;
    var lo = 0;
    var hi = length - 1;
    while (lo <= hi) {
      var mid = lo + FIBSEQ.get(k) - 1;
      if (safeGet(arr, mid) < key) {
        k = k - 1;
        lo = mid + 1;
      } else if (safeGet(arr, mid) > key) {
        k = k - 2;
        hi = mid - 1;
      } else {
        return mid >= length ? length - 1 : mid;
      }
    }
    return -(lo + 1);
  }

  public static OptionalLong ceil(long[] arr, long key) {
    var i = fibSearch(arr, key);
    if (i >= 0) {
      return OptionalLong.of(key);
    }
    i = -i - 1;
    if (i < arr.length) {
      return OptionalLong.of(arr[i]);
    }
    return OptionalLong.empty();
  }

  public static OptionalLong floor(long[] arr, long key) {
    var i = fibSearch(arr, key);
    if (i >= 0) {
      return OptionalLong.of(key);
    }
    i = -i - 2;
    if (i >= 0) {
      return OptionalLong.of(safeGet(arr, i));
    }
    return OptionalLong.empty();
  }

  private static long safeGet(long[] arr, int index) {
    if (index >= arr.length) {
      return arr[arr.length - 1];
    } else if (index >= 0) {
      return arr[index];
    } else {
      return arr[0];
    }
  }

  public static <T> int fibSearch(T[] arr, T key, Comparator<? super T> c) {
    var length = arr.length;
    if (length == 1) {
      return arr[0] == key ? 0 : -1;
    }
    var k = getFibIndex(length) - 1;
    var lo = 0;
    var hi = length - 1;
    while (lo <= hi) {
      var mid = lo + FIBSEQ.get(k) - 1;
      if (c.compare(safeGet(arr, mid), key) < 0) {
        k = k - 1;
        lo = mid + 1;
      } else if (c.compare(safeGet(arr, mid), key) > 0) {
        k = k - 2;
        hi = mid - 1;
      } else {
        return mid >= length ? length - 1 : mid;
      }
    }
    return -(lo + 1);
  }

  public static <T> Optional<T> ceil(T[] arr, T key, Comparator<? super T> c) {
    var i = fibSearch(arr, key, c);
    if (i >= 0) {
      return Optional.of(key);
    }
    i = -i - 1;
    if (i < arr.length) {
      return Optional.of(arr[i]);
    }
    return Optional.empty();
  }

  public static <T> Optional<T> floor(T[] arr, T key, Comparator<? super T> c) {
    var i = fibSearch(arr, key, c);
    if (i >= 0) {
      return Optional.of(key);
    }
    i = -i - 2;
    if (i >= 0) {
      return Optional.of(safeGet(arr, i));
    }
    return Optional.empty();
  }

  private static <T> T safeGet(T[] arr, int index) {
    if (index >= arr.length) {
      return arr[arr.length - 1];
    } else if (index >= 0) {
      return arr[index];
    } else {
      return arr[0];
    }
  }

  public static <T> int fibSearch(List<T> list, T key, Comparator<? super T> c) {
    var length = list.size();
    if (length == 1) {
      return list.get(0) == key ? 0 : -1;
    }
    var k = getFibIndex(length) - 1;
    var lo = 0;
    var hi = length - 1;
    while (lo <= hi) {
      var mid = lo + FIBSEQ.get(k) - 1;
      if (c.compare(safeGet(list, mid), key) < 0) {
        k = k - 1;
        lo = mid + 1;
      } else if (c.compare(safeGet(list, mid), key) > 0) {
        k = k - 2;
        hi = mid - 1;
      } else {
        return mid >= length ? length - 1 : mid;
      }
    }
    return -(lo + 1);
  }

  public static <T> Optional<T> ceil(List<T> list, T key, Comparator<? super T> c) {
    var i = fibSearch(list, key, c);
    if (i >= 0) {
      return Optional.of(key);
    }
    i = -i - 1;
    if (i < list.size()) {
      return Optional.of(list.get(i));
    }
    return Optional.empty();
  }

  public static <T> Optional<T> floor(List<T> list, T key, Comparator<? super T> c) {
    var i = fibSearch(list, key, c);
    if (i >= 0) {
      return Optional.of(key);
    }
    i = -i - 2;
    if (i >= 0) {
      return Optional.of(safeGet(list, i));
    }
    return Optional.empty();
  }

  private static <T> T safeGet(List<T> list, int index) {
    if (index >= list.size()) {
      return list.get(list.size() - 1);
    } else if (index >= 0) {
      return list.get(index);
    } else {
      return list.get(0);
    }
  }
}
