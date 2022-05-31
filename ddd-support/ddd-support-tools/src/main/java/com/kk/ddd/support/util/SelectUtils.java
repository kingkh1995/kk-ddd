package com.kk.ddd.support.util;

import java.util.Comparator;
import java.util.List;

/**
 * 选择工具类
 *
 * @author KaiKoo
 */
public final class SelectUtils {

  private SelectUtils() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static void rangeCheck(int length, int index) {
    if (index >= length || index < 0) {
      throw new IllegalArgumentException();
    }
  }

  private static void swap(int[] arr, int a, int b) {
    if (a >= arr.length || a < 0 || b >= arr.length || b < 0) {
      throw new IllegalArgumentException();
    }
    if (a == b) {
      return;
    }
    var temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }

  /** 快速选择出最大的元素 */
  public static int selectMax(int[] arr) {
    return selectKthL(arr, 1);
  }

  /** 快速选择出最小的元素 */
  public static int selectMin(int[] arr) {
    return selectKthS(arr, 1);
  }

  /** 快速选择出第k大的元素 */
  public static int selectKthL(int[] arr, int k) {
    return quickSelect0(arr, arr.length - k);
  }

  /** 快速选择出第k小的元素 */
  public static int selectKthS(int[] arr, int k) {
    return quickSelect0(arr, k - 1);
  }

  /**
   * 快速选择出排序后index位置的元素
   *
   * @param arr 未排序数组
   * @param index 索引
   * @return 元素值
   */
  private static int quickSelect0(int[] arr, int index) {
    var length = arr.length;
    // max min 直接遍历查找
    if (index == 0) {
      var min = arr[0];
      for (var n : arr) {
        if (n < min) {
          min = n;
        }
      }
      return min;
    } else if (index == length - 1) {
      var max = arr[0];
      for (var n : arr) {
        if (n > max) {
          max = n;
        }
      }
      return max;
    }
    // 使用快速三向切分快排查找
    rangeCheck(length, index);
    var lo = 0;
    var hi = length - 1;
    while (true) {
      // 小于等于5个元素使用插入排序
      if (hi - lo < 5) {
        for (var i = lo + 1; i <= hi; i++) {
          var j = i;
          var n = arr[j];
          for (; j > lo && arr[j - 1] > n; j--) {
            arr[j] = arr[j - 1];
          }
          arr[j] = n;
        }
        return arr[index];
      }
      // 将index处的值作为pivot
      swap(arr, index, lo);
      // 切分为：lo--p--i--j--q--hi
      var i = lo;
      var j = hi + 1;
      var p = lo; // 左边等于区间是[lo,lo]至少包含一个元素
      var q = hi + 1; // 右边等于区间为[hi+1,hi]可能没有元素
      var pivot = arr[lo];
      while (true) {
        // 从左边开始找到第一个大于等于切分值的数
        while (arr[++i] < pivot) {
          if (i == hi) {
            break;
          }
        }
        // 从右边开始找到第一个小于等于切分值的数
        while (arr[--j] > pivot) {}
        // 判断是否相遇 在中间相遇则必然等于pivot 在最后一个元素相遇则不一定（切分元素刚好是最大值）
        if (i == j && arr[j] == pivot) {
          // 如果等于pivot 则交换到左边保证j必然小于pivot
          swap(arr, ++p, j);
        }
        if (i >= j) {
          break; // 循环终止，经过上面的判断后[i,j]区间已完全排序
        }
        // 循环未终止则和普通的快速排序一样，交换i j
        swap(arr, i, j);
        // 交换完再判断是否等于 等于则交换到两端的等于区间 小于区间右移 大于区间左移
        if (arr[i] == pivot) {
          swap(arr, ++p, i);
        }
        if (arr[j] == pivot) {
          swap(arr, --q, j);
        }
      }
      i = j + 1;
      // 循环结束 此时(p, j]区间小于pivot [i, q)区间大于pivot
      // 先计算出交换后等于pivot的区间的两端
      var left = lo + (j - p); // 左端点
      var right = hi - (q - i); // 右断点
      // 如果index在等于区间内 表示结果为pivot直接返回
      if (index >= left && index <= right) {
        return pivot;
      }
      // 交换等于的区间到中间
      for (var t = lo; t <= p; t++) {
        swap(arr, t, j--);
      }
      for (var t = hi; t >= q; t--) {
        swap(arr, t, i++);
      }
      // 交换完之后，[lo, j]小于pivot [i, hi]大于pivot
      if (index <= j) {
        hi = j;
      } else if (index >= i) {
        lo = i;
      }
    }
  }

  private static void swap(long[] arr, int a, int b) {
    if (a >= arr.length || a < 0 || b >= arr.length || b < 0) {
      throw new IllegalArgumentException();
    }
    if (a == b) {
      return;
    }
    var temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }

  public static long selectMax(long[] arr) {
    return selectKthL(arr, 1);
  }

  public static long selectMin(long[] arr) {
    return selectKthS(arr, 1);
  }

  public static long selectKthL(long[] arr, int k) {
    return quickSelect0(arr, arr.length - k);
  }

  public static long selectKthS(long[] arr, int k) {
    return quickSelect0(arr, k - 1);
  }

  private static long quickSelect0(long[] arr, int index) {
    var length = arr.length;
    if (index == 0) {
      var min = arr[0];
      for (var n : arr) {
        if (n < min) {
          min = n;
        }
      }
      return min;
    } else if (index == length - 1) {
      var max = arr[0];
      for (var n : arr) {
        if (n > max) {
          max = n;
        }
      }
      return max;
    }
    rangeCheck(length, index);
    var lo = 0;
    var hi = length - 1;
    while (true) {
      if (hi - lo < 5) {
        for (var i = lo + 1; i <= hi; i++) {
          var j = i;
          var n = arr[j];
          for (; j > lo && arr[j - 1] > n; j--) {
            arr[j] = arr[j - 1];
          }
          arr[j] = n;
        }
        return arr[index];
      }
      swap(arr, index, lo);
      var i = lo;
      var j = hi + 1;
      var p = lo;
      var q = hi + 1;
      var pivot = arr[lo];
      while (true) {
        while (arr[++i] < pivot) {
          if (i == hi) {
            break;
          }
        }
        while (arr[--j] > pivot) {}
        if (i == j && arr[j] == pivot) {
          swap(arr, ++p, j);
        }
        if (i >= j) {
          break;
        }
        swap(arr, i, j);
        if (arr[i] == pivot) {
          swap(arr, ++p, i);
        }
        if (arr[j] == pivot) {
          swap(arr, --q, j);
        }
      }
      i = j + 1;
      var left = lo + (j - p);
      var right = hi - (q - i);
      if (index >= left && index <= right) {
        return pivot;
      }
      for (var t = lo; t <= p; t++) {
        swap(arr, t, j--);
      }
      for (var t = hi; t >= q; t--) {
        swap(arr, t, i++);
      }
      if (index <= j) {
        hi = j;
      } else if (index >= i) {
        lo = i;
      }
    }
  }

  private static <T> void swap(T[] arr, int a, int b) {
    if (a >= arr.length || a < 0 || b >= arr.length || b < 0) {
      throw new IllegalArgumentException();
    }
    if (a == b) {
      return;
    }
    var temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }

  public static <T> T selectMax(T[] arr, Comparator<? super T> c) {
    return selectKthL(arr, 1, c);
  }

  public static <T> T selectMin(T[] arr, Comparator<? super T> c) {
    return selectKthS(arr, 1, c);
  }

  public static <T> T selectKthL(T[] arr, int k, Comparator<? super T> c) {
    return quickSelect0(arr, arr.length - k, c);
  }

  public static <T> T selectKthS(T[] arr, int k, Comparator<? super T> c) {
    return quickSelect0(arr, k - 1, c);
  }

  private static <T> T quickSelect0(T[] arr, int index, Comparator<? super T> c) {
    var length = arr.length;
    if (index == 0) {
      var min = arr[0];
      for (var n : arr) {
        if (c.compare(n, min) < 0) {
          min = n;
        }
      }
      return min;
    } else if (index == length - 1) {
      var max = arr[0];
      for (var n : arr) {
        if (c.compare(n, max) > 0) {
          max = n;
        }
      }
      return max;
    }
    rangeCheck(length, index);
    var lo = 0;
    var hi = length - 1;
    while (true) {
      if (hi - lo < 5) {
        for (var i = lo + 1; i <= hi; i++) {
          var j = i;
          var n = arr[j];
          for (; j > lo && c.compare(arr[j - 1], n) > 0; j--) {
            arr[j] = arr[j - 1];
          }
          arr[j] = n;
        }
        return arr[index];
      }
      swap(arr, index, lo);
      var i = lo;
      var j = hi + 1;
      var p = lo;
      var q = hi + 1;
      var pivot = arr[lo];
      while (true) {
        while (c.compare(arr[++i], pivot) < 0) {
          if (i == hi) {
            break;
          }
        }
        while (c.compare(arr[--j], pivot) > 0) {}
        if (i == j && c.compare(arr[j], pivot) == 0) {
          swap(arr, ++p, j);
        }
        if (i >= j) {
          break;
        }
        swap(arr, i, j);
        if (c.compare(arr[i], pivot) == 0) {
          swap(arr, ++p, i);
        }
        if (c.compare(arr[j], pivot) == 0) {
          swap(arr, --q, j);
        }
      }
      i = j + 1;
      var left = lo + (j - p);
      var right = hi - (q - i);
      if (index >= left && index <= right) {
        return pivot;
      }
      for (var t = lo; t <= p; t++) {
        swap(arr, t, j--);
      }
      for (var t = hi; t >= q; t--) {
        swap(arr, t, i++);
      }
      if (index <= j) {
        hi = j;
      } else if (index >= i) {
        lo = i;
      }
    }
  }

  private static <T> void swap(List<T> list, int a, int b) {
    if (a >= list.size() || a < 0 || b >= list.size() || b < 0) {
      throw new IllegalArgumentException();
    }
    if (a == b) {
      return;
    }
    var temp = list.get(a);
    list.set(a, list.get(b));
    list.set(b, temp);
  }

  public static <T> T selectMax(List<T> list, Comparator<? super T> c) {
    return selectKthL(list, 1, c);
  }

  public static <T> T selectMin(List<T> list, Comparator<? super T> c) {
    return selectKthS(list, 1, c);
  }

  public static <T> T selectKthL(List<T> list, int k, Comparator<? super T> c) {
    return quickSelect0(list, list.size() - k, c);
  }

  public static <T> T selectKthS(List<T> list, int k, Comparator<? super T> c) {
    return quickSelect0(list, k - 1, c);
  }

  private static <T> T quickSelect0(List<T> list, int index, Comparator<? super T> c) {
    var length = list.size();
    if (index == 0) {
      var min = list.get(0);
      for (var n : list) {
        if (c.compare(n, min) < 0) {
          min = n;
        }
      }
      return min;
    } else if (index == length - 1) {
      var max = list.get(0);
      for (var n : list) {
        if (c.compare(n, max) > 0) {
          max = n;
        }
      }
      return max;
    }
    rangeCheck(length, index);
    var lo = 0;
    var hi = length - 1;
    while (true) {
      if (hi - lo < 5) {
        for (var i = lo + 1; i <= hi; i++) {
          var j = i;
          var n = list.get(j);
          for (; j > lo && c.compare(list.get(j - 1), n) > 0; j--) {
            list.set(j, list.get(j - 1));
          }
          list.set(j, n);
        }
        return list.get(index);
      }
      swap(list, index, lo);
      var i = lo;
      var j = hi + 1;
      var p = lo;
      var q = hi + 1;
      var pivot = list.get(lo);
      while (true) {
        while (c.compare(list.get(++i), pivot) < 0) {
          if (i == hi) {
            break;
          }
        }
        while (c.compare(list.get(--j), pivot) > 0) {}
        if (i == j && c.compare(list.get(j), pivot) == 0) {
          swap(list, ++p, j);
        }
        if (i >= j) {
          break;
        }
        swap(list, i, j);
        if (c.compare(list.get(i), pivot) == 0) {
          swap(list, ++p, i);
        }
        if (c.compare(list.get(j), pivot) == 0) {
          swap(list, --q, j);
        }
      }
      i = j + 1;
      var left = lo + (j - p);
      var right = hi - (q - i);
      if (index >= left && index <= right) {
        return pivot;
      }
      for (var t = lo; t <= p; t++) {
        swap(list, t, j--);
      }
      for (var t = hi; t >= q; t--) {
        swap(list, t, i++);
      }
      if (index <= j) {
        hi = j;
      } else if (index >= i) {
        lo = i;
      }
    }
  }
}
