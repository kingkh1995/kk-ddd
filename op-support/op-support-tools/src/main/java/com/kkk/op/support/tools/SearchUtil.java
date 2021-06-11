package com.kkk.op.support.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author KaiKoo
 */
public final class SearchUtil {

    private SearchUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    private static final List<Integer> FIBSEQ = new CopyOnWriteArrayList<>(
            new Integer[]{1, 1, 2, 3, 5, 8, 13, 21, 34, 55});

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

    private synchronized static void fibGrow(int length) {
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

    private static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }

    /**
     * 斐波那契查找 效率与二分查找一致 对磁盘比较友好
     */
    public static int fbSearch(int[] arr, int key) {
        return fbSearch0(arr, 0, arr.length - 1, key);
    }

    public static int fbSearch(int[] arr, int fromIndex, int toIndex, int key) {
        rangeCheck(arr.length, fromIndex, toIndex);
        return fbSearch0(arr, fromIndex, toIndex--, key);
    }

    private static int fbSearch0(int[] arr, int lo, int hi, int key) {
        var length = arr.length;
        if (length == 1) {
            return arr[0] == key ? 0 : -1;
        }
        var k = getFibIndex(length) - 1;
        while (lo <= hi) {
            var mid = lo + FIBSEQ.get(k) - 1;
            if (safeGet(arr, mid) < key) {
                k = k - 1;
                lo = mid + 1;
            } else if (safeGet(arr, mid) > key) {
                k = k - 2;
                hi = mid - 1;
            } else {
                return mid > length ? length - 1 : mid;
            }
        }
        return -1;
    }

    // 安全获取数组元素，超出范围取两端
    private static int safeGet(int[] arr, int index) {
        if (index < 0) {
            return arr[0];
        } else if (index >= arr.length) {
            return arr[arr.length - 1];
        } else {
            return arr[index];
        }
    }

    public static int fbSearch(long[] arr, int key) {
        return fbSearch0(arr, 0, arr.length - 1, key);
    }

    public static int fbSearch(long[] arr, int fromIndex, int toIndex, int key) {
        rangeCheck(arr.length, fromIndex, toIndex);
        return fbSearch0(arr, fromIndex, toIndex--, key);
    }

    private static int fbSearch0(long[] arr, int lo, int hi, int key) {
        var length = arr.length;
        if (length == 1) {
            return arr[0] == key ? 0 : -1;
        }
        var k = getFibIndex(length) - 1;
        while (lo <= hi) {
            var mid = lo + FIBSEQ.get(k) - 1;
            if (safeGet(arr, mid) < key) {
                k = k - 1;
                lo = mid + 1;
            } else if (safeGet(arr, mid) > key) {
                k = k - 2;
                hi = mid - 1;
            } else {
                return mid > length ? length - 1 : mid;
            }
        }
        return -1;
    }

    private static long safeGet(long[] arr, int index) {
        if (index < 0) {
            return arr[0];
        } else if (index >= arr.length) {
            return arr[arr.length - 1];
        } else {
            return arr[index];
        }
    }

    public static <T> int fbSearch(T[] arr, T key, Comparator<? super T> c) {
        return fbSearch0(arr, 0, arr.length - 1, key, c);
    }

    public static <T> int fbSearch(T[] arr, int fromIndex, int toIndex, T key,
            Comparator<? super T> c) {
        rangeCheck(arr.length, fromIndex, toIndex);
        return fbSearch0(arr, fromIndex, toIndex--, key, c);
    }

    private static <T> int fbSearch0(T[] arr, int lo, int hi, T key, Comparator<? super T> c) {
        var length = arr.length;
        if (length == 1) {
            return arr[0] == key ? 0 : -1;
        }
        var k = getFibIndex(length) - 1;
        while (lo <= hi) {
            var mid = lo + FIBSEQ.get(k) - 1;
            if (c.compare(safeGet(arr, mid), key) < 0) {
                k = k - 1;
                lo = mid + 1;
            } else if (c.compare(safeGet(arr, mid), key) > 0) {
                k = k - 2;
                hi = mid - 1;
            } else {
                return mid > length ? length - 1 : mid;
            }
        }
        return -1;
    }

    private static <T> T safeGet(T[] arr, int index) {
        if (index < 0) {
            return arr[0];
        } else if (index >= arr.length) {
            return arr[arr.length - 1];
        } else {
            return arr[index];
        }
    }

    public static <T> int fbSearch(List<T> list, T key, Comparator<? super T> c) {
        return fbSearch0(list, 0, list.size() - 1, key, c);
    }

    public static <T> int fbSearch(List<T> list, int fromIndex, int toIndex, T key,
            Comparator<? super T> c) {
        rangeCheck(list.size(), fromIndex, toIndex);
        return fbSearch0(list, fromIndex, toIndex--, key, c);
    }

    private static <T> int fbSearch0(List<T> list, int lo, int hi, T key, Comparator<? super T> c) {
        var length = list.size();
        if (length == 1) {
            return list.get(0) == key ? 0 : -1;
        }
        var k = getFibIndex(length) - 1;
        while (lo <= hi) {
            var mid = lo + FIBSEQ.get(k) - 1;
            if (c.compare(safeGet(list, mid), key) < 0) {
                k = k - 1;
                lo = mid + 1;
            } else if (c.compare(safeGet(list, mid), key) > 0) {
                k = k - 2;
                hi = mid - 1;
            } else {
                return mid > length ? length - 1 : mid;
            }
        }
        return -1;
    }

    private static <T> T safeGet(List<T> list, int index) {
        if (index < 0) {
            return list.get(0);
        } else if (index >= list.size()) {
            return list.get(list.size() - 1);
        } else {
            return list.get(index);
        }
    }

}
