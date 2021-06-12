package com.kkk.op.support.tools;

import java.util.Comparator;
import java.util.List;

/**
 * 选择工具类
 *
 * @author KaiKoo
 */
public final class SelectUtil {

  private SelectUtil() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static void rangeCheck(int arrayLength, int k) {
    if (k > arrayLength || k < 1) {
      throw new IllegalArgumentException();
    }
  }

  private static void swap(int[] arr, int a, int b) {
    if (a >= arr.length || a < 0 || b >= arr.length || b < 0) {
      throw new IllegalArgumentException();
    }
    var temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }

  public static int selectMax(int[] arr) {
    return quickSelect(arr, arr.length);
  }

  public static int selectMin(int[] arr) {
    return quickSelect(arr, 1);
  }

  /** 快速选择出第k小的元素 */
  public static int quickSelect(int[] arr, int k) {
    // max 和 min时直接遍历查找
    if (k == 1) {
      var min = arr[0];
      for (var n : arr) {
        if (n < min) {
          min = n;
        }
      }
      return min;
    } else if (k == arr.length) {
      var max = arr[0];
      for (var n : arr) {
        if (n > max) {
          max = n;
        }
      }
      return max;
    }
    rangeCheck(arr.length, k);
    return quickSelect(arr, k - 1, 0, arr.length - 1);
  }

  private static int quickSelect(int[] arr, int index, int lo, int hi) {
    while (true) {
      // 少于五个元素直接进行插入排序
      if (hi - lo + 1 < 5) {
        var n = hi - lo + 1;
        for (var i = lo; i - lo < n; i++) {
          for (var j = i; j > 0 && arr[j] < arr[j - 1]; --j) {
            swap(arr, j, j - 1);
          }
        }
        return arr[index];
      }
      // 使用快速三向切分快排
      swap(arr, index, lo);
      var i = lo;
      var j = hi + 1;
      var p = lo;
      var q = hi + 1;
      var pivot = arr[lo];
      while (true) {
        // 从左边开始找到第一个大于等于pivot的数
        while (arr[++i] < pivot) {
          if (i == hi) {
            break;
          }
        }
        // 从右边开始找到第一个小于等于pivot的数
        while (arr[--j] > pivot) {
          if (j == lo) {
            break;
          }
        }
        // 相遇有两种情况，在中间相遇，肯定等于pivot，第二种情况在hi处相遇，此时不一定等于。
        // 如果i j 相遇，且等于pivot，则交换到等于pivot的区间
        if (i == j && arr[i] == pivot) {
          swap(arr, ++p, i);
        }
        if (i >= j) {
          break;
        }
        // 和普通的partiion方法一样，交换i j
        swap(arr, i, j);
        if (arr[i] == pivot) {
          swap(arr, ++p, i);
        }
        if (arr[j] == pivot) {
          swap(arr, --q, j);
        }
      }
      // 循环终止时，j处的数必定小于pivot，因为如果等于pivot会和小于pivot的数交换
      // 故此时(p, j]区间小于pivot [j+1, q)区间大于pivot
      // 先判断不交换
      i = j + 1;
      var left = j - p + lo;
      var right = j + hi - q + 1 + j;
      if (left <= index && right >= index) {
        return pivot;
      }
      // 交换等于的区间到中间
      for (int k = lo; k <= p; k++) {
        swap(arr, k, j--);
      }
      for (int k = hi; k >= q; k--) {
        swap(arr, k, i++);
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
    var temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }

  public static long selectMax(long[] arr) {
    return quickSelect(arr, arr.length);
  }

  public static long selectMin(long[] arr) {
    return quickSelect(arr, 1);
  }

  public static long quickSelect(long[] arr, int k) {
    // max 和 min时直接遍历查找
    if (k == 1) {
      var min = arr[0];
      for (var n : arr) {
        if (n < min) {
          min = n;
        }
      }
      return min;
    } else if (k == arr.length) {
      var max = arr[0];
      for (var n : arr) {
        if (n > max) {
          max = n;
        }
      }
      return max;
    }
    rangeCheck(arr.length, k);
    return quickSelect(arr, k - 1, 0, arr.length - 1);
  }

  private static long quickSelect(long[] arr, int index, int lo, int hi) {
    while (true) {
      if (hi - lo + 1 < 5) {
        var n = hi - lo + 1;
        for (var i = lo; i - lo < n; i++) {
          for (var j = i; j > 0 && arr[j] < arr[j - 1]; --j) {
            swap(arr, j, j - 1);
          }
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
        while (arr[--j] > pivot) {
          if (j == lo) {
            break;
          }
        }
        if (i == j && arr[i] == pivot) {
          swap(arr, ++p, i);
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
      var left = j - p + lo;
      var right = j + hi - q + 1;
      if (left <= index && right >= index) {
        return pivot;
      }
      for (int k = lo; k <= p; k++) {
        swap(arr, k, j--);
      }
      for (int k = hi; k >= q; k--) {
        swap(arr, k, i++);
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
    var temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }

  public static <T> T selectMax(T[] arr, Comparator<? super T> c) {
    return quickSelect(arr, arr.length, c);
  }

  public static <T> T selectMin(T[] arr, Comparator<? super T> c) {
    return quickSelect(arr, 1, c);
  }

  public static <T> T quickSelect(T[] arr, int k, Comparator<? super T> c) {
    // max 和 min时直接遍历查找
    if (k == 1) {
      var min = arr[0];
      for (var n : arr) {
        if (c.compare(n, min) < 0) {
          min = n;
        }
      }
      return min;
    } else if (k == arr.length) {
      var max = arr[0];
      for (var n : arr) {
        if (c.compare(n, max) > 0) {
          max = n;
        }
      }
      return max;
    }
    rangeCheck(arr.length, k);
    return quickSelect(arr, k - 1, 0, arr.length - 1, c);
  }

  private static <T> T quickSelect(T[] arr, int index, int lo, int hi, Comparator<? super T> c) {
    while (true) {
      if (hi - lo + 1 < 5) {
        var n = hi - lo + 1;
        for (var i = lo; i - lo < n; i++) {
          for (var j = i; j > 0 && c.compare(arr[j], arr[j - 1]) < 0; --j) {
            swap(arr, j, j - 1);
          }
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
        while (c.compare(arr[--j], pivot) > 0) {
          if (j == lo) {
            break;
          }
        }
        if (i == j && arr[i] == pivot) {
          swap(arr, ++p, i);
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
      var left = j - p + lo;
      var right = j + hi - q + 1;
      if (left <= index && right >= index) {
        return pivot;
      }
      for (int k = lo; k <= p; k++) {
        swap(arr, k, j--);
      }
      for (int k = hi; k >= q; k--) {
        swap(arr, k, i++);
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
    var temp = list.get(a);
    list.set(a, list.get(b));
    list.set(b, temp);
  }

  public static <T> T selectMax(List<T> list, Comparator<? super T> c) {
    return quickSelect(list, list.size(), c);
  }

  public static <T> T selectMin(List<T> list, Comparator<? super T> c) {
    return quickSelect(list, 1, c);
  }

  public static <T> T quickSelect(List<T> list, int k, Comparator<? super T> c) {
    // max 和 min时直接遍历查找
    if (k == 1) {
      var min = list.get(0);
      for (var n : list) {
        if (c.compare(n, min) < 0) {
          min = n;
        }
      }
      return min;
    } else if (k == list.size()) {
      var max = list.get(0);
      for (var n : list) {
        if (c.compare(n, max) > 0) {
          max = n;
        }
      }
      return max;
    }
    rangeCheck(list.size(), k);
    return quickSelect(list, k - 1, 0, list.size() - 1, c);
  }

  private static <T> T quickSelect(
      List<T> list, int index, int lo, int hi, Comparator<? super T> c) {
    while (true) {
      if (hi - lo + 1 < 5) {
        var n = hi - lo + 1;
        for (var i = lo; i - lo < n; i++) {
          for (var j = i; j > 0 && c.compare(list.get(j), list.get(j - 1)) < 0; --j) {
            swap(list, j, j - 1);
          }
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
        while (c.compare(list.get(--j), pivot) > 0) {
          if (j == lo) {
            break;
          }
        }
        if (i == j && list.get(i) == pivot) {
          swap(list, ++p, i);
        }
        if (i >= j) {
          break;
        }
        swap(list, i, j);
        if (list.get(i) == pivot) {
          swap(list, ++p, i);
        }
        if (list.get(j) == pivot) {
          swap(list, --q, j);
        }
      }
      i = j + 1;
      var left = j - p + lo;
      var right = j + hi - q + 1;
      if (left <= index && right >= index) {
        return pivot;
      }
      for (int k = lo; k <= p; k++) {
        swap(list, k, j--);
      }
      for (int k = hi; k >= q; k--) {
        swap(list, k, i++);
      }
      if (index <= j) {
        hi = j;
      } else if (index >= i) {
        lo = i;
      }
    }
  }
}
