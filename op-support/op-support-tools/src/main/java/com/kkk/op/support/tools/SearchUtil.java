package com.kkk.op.support.tools;

import java.util.Comparator;
import java.util.List;

/**
 * todo... 转为Comparable 集合查找
 *
 * @author KaiKoo
 */
public final class SearchUtil {

    private SearchUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    //==============================================================================================

    /**
     * 斐波那契查找 效率与二分查找一致 对磁盘比较友好
     */

    static {
        // 最大 121393
        var arr = new int[26];
        arr[0] = 1;
        arr[1] = 1;
        for (var i = 2; i < arr.length; i++) {
            arr[i] = arr[i - 1] + arr[i - 2];
        }
        fbArray = arr;
    }

    private static final int[] fbArray;

    // 返回大于等于 length 的第一个斐波那契数的索引
    private static int getFbIndex(int length) {
        if (length > fbArray[25]) {
            throw new IllegalArgumentException();
        }
        var i = 0;
        while (fbArray[i++] < length) {
        }
        return --i;
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
        var k = getFbIndex(length) - 1;
        while (lo <= hi) {
            var mid = lo + fbArray[k] - 1;
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
        var k = getFbIndex(length) - 1;
        while (lo <= hi) {
            var mid = lo + fbArray[k] - 1;
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
        var k = getFbIndex(length) - 1;
        while (lo <= hi) {
            var mid = lo + fbArray[k] - 1;
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
        var k = getFbIndex(length) - 1;
        while (lo <= hi) {
            var mid = lo + fbArray[k] - 1;
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

    //==============================================================================================

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

    public static int quickSelect(int[] arr, int k) {
        rangeCheck(arr.length, k);
        // k < 5 或 k > arr.length - 5 使用冒泡排序
        if (k < 5) {
            var isSorted = false;
            for (var i = 0; i < k && !isSorted; i++) {
                isSorted = true;
                for (var j = arr.length - 1; j > i; j--) {
                    if (arr[j] < arr[j - 1]) {
                        isSorted = false;
                        swap(arr, j - 1, j);
                    }
                }
            }
            return arr[k - 1];
        } else if (k > arr.length - 5) {
            var isSorted = false;
            for (var i = arr.length - 1; i > arr.length - 1 - k && !isSorted; i--) {
                isSorted = true;
                for (var j = 0; j < i; j++) {
                    if (arr[j] > arr[j + 1]) {
                        isSorted = false;
                        swap(arr, j, j + 1);
                    }
                }
            }
            return arr[k - 1];
        }
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
            // 交换等于的区间到中间
            // 循环终止时，j处的数必定小于pivot，因为如果等于pivot会和小于pivot的数交换
            // 故此时(p, j]区间小于pivot [j+1, q)区间大于pivot
            i = j + 1;
            var left = j - p + lo;
            var right = hi - q + 1 + j;
            if (left <= index && right >= index) {
                return pivot;
            }
            for (int k = lo; k <= p; k++) {
                swap(arr, k, j--);
            }
            for (int k = hi; k >= q; k--) {
                swap(arr, k, i++);
            }
            // [lo, j]小于 [i, hi]大于
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

    public static long quickSelect(long[] arr, int k) {
        rangeCheck(arr.length, k);
        // k < 5 或 k > arr.length - 5 使用冒泡排序
        if (k < 5) {
            var isSorted = false;
            for (var i = 0; i < k && !isSorted; i++) {
                isSorted = true;
                for (var j = arr.length - 1; j > i; j--) {
                    if (arr[j] < arr[j - 1]) {
                        isSorted = false;
                        swap(arr, j - 1, j);
                    }
                }
            }
            return arr[k - 1];
        } else if (k > arr.length - 5) {
            var isSorted = false;
            for (var i = arr.length - 1; i > arr.length - 1 - k && !isSorted; i--) {
                isSorted = true;
                for (var j = 0; j < i; j++) {
                    if (arr[j] > arr[j + 1]) {
                        isSorted = false;
                        swap(arr, j, j + 1);
                    }
                }
            }
            return arr[k - 1];
        }
        return quickSelect(arr, k - 1, 0, arr.length - 1);
    }

    private static long quickSelect(long[] arr, int index, int lo, int hi) {
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
            // 交换等于的区间到中间
            // 循环终止时，j处的数必定小于pivot，因为如果等于pivot会和小于pivot的数交换
            // 故此时(p, j]区间小于pivot [j+1, q)区间大于pivot
            i = j + 1;
            var left = j - p + lo;
            var right = hi - q + 1 + j;
            if (left <= index && right >= index) {
                return pivot;
            }
            for (int k = lo; k <= p; k++) {
                swap(arr, k, j--);
            }
            for (int k = hi; k >= q; k--) {
                swap(arr, k, i++);
            }
            // [lo, j]小于 [i, hi]大于
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

    public static <T> T quickSelect(T[] arr, int k, Comparator<? super T> c) {
        rangeCheck(arr.length, k);
        // k < 5 或 k > arr.length - 5 使用冒泡排序
        if (k < 5) {
            var isSorted = false;
            for (var i = 0; i < k && !isSorted; i++) {
                isSorted = true;
                for (var j = arr.length - 1; j > i; j--) {
                    if (c.compare(arr[j], arr[j - 1]) < 0) {
                        isSorted = false;
                        swap(arr, j - 1, j);
                    }
                }
            }
            return arr[k - 1];
        } else if (k > arr.length - 5) {
            var isSorted = false;
            for (var i = arr.length - 1; i > arr.length - 1 - k && !isSorted; i--) {
                isSorted = true;
                for (var j = 0; j < i; j++) {
                    if (c.compare(arr[j], arr[j + 1]) > 0) {
                        isSorted = false;
                        swap(arr, j, j + 1);
                    }
                }
            }
            return arr[k - 1];
        }
        return quickSelect(arr, k - 1, 0, arr.length - 1, c);
    }

    private static <T> T quickSelect(T[] arr, int index, int lo, int hi, Comparator<? super T> c) {
        while (true) {
            // 少于五个元素直接进行插入排序
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
                // 从左边开始找到第一个大于等于pivot的数
                while (c.compare(arr[++i], pivot) < 0) {
                    if (i == hi) {
                        break;
                    }
                }
                // 从右边开始找到第一个小于等于pivot的数
                while (c.compare(arr[--j], pivot) > 0) {
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
            // 交换等于的区间到中间
            // 循环终止时，j处的数必定小于pivot，因为如果等于pivot会和小于pivot的数交换
            // 故此时(p, j]区间小于pivot [j+1, q)区间大于pivot
            i = j + 1;
            var left = j - p + lo;
            var right = hi - q + 1 + j;
            if (left <= index && right >= index) {
                return pivot;
            }
            for (int k = lo; k <= p; k++) {
                swap(arr, k, j--);
            }
            for (int k = hi; k >= q; k--) {
                swap(arr, k, i++);
            }
            // [lo, j]小于 [i, hi]大于
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

    public static <T> T quickSelect(List<T> list, int k, Comparator<? super T> c) {
        rangeCheck(list.size(), k);
        // k < 5 或 k > arr.length - 5 使用冒泡排序
        if (k < 5) {
            var isSorted = false;
            for (var i = 0; i < k && !isSorted; i++) {
                isSorted = true;
                for (var j = list.size() - 1; j > i; j--) {
                    if (c.compare(list.get(j), list.get(j - 1)) < 0) {
                        isSorted = false;
                        swap(list, j - 1, j);
                    }
                }
            }
            return list.get(k - 1);
        } else if (k > list.size() - 5) {
            var isSorted = false;
            for (var i = list.size() - 1; i > list.size() - 1 - k && !isSorted; i--) {
                isSorted = true;
                for (var j = 0; j < i; j++) {
                    if (c.compare(list.get(j), list.get(j + 1)) > 0) {
                        isSorted = false;
                        swap(list, j, j + 1);
                    }
                }
            }
            return list.get(k - 1);
        }
        return quickSelect(list, k - 1, 0, list.size() - 1, c);
    }

    private static <T> T quickSelect(List<T> list, int index, int lo, int hi,
            Comparator<? super T> c) {
        while (true) {
            // 少于五个元素直接进行插入排序
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
                // 从左边开始找到第一个大于等于pivot的数
                while (c.compare(list.get(++i), pivot) < 0) {
                    if (i == hi) {
                        break;
                    }
                }
                // 从右边开始找到第一个小于等于pivot的数
                while (c.compare(list.get(--j), pivot) > 0) {
                    if (j == lo) {
                        break;
                    }
                }
                // 相遇有两种情况，在中间相遇，肯定等于pivot，第二种情况在hi处相遇，此时不一定等于。
                // 如果i j 相遇，且等于pivot，则交换到等于pivot的区间
                if (i == j && list.get(i) == pivot) {
                    swap(list, ++p, i);
                }
                if (i >= j) {
                    break;
                }
                // 和普通的partiion方法一样，交换i j
                swap(list, i, j);
                if (list.get(i) == pivot) {
                    swap(list, ++p, i);
                }
                if (list.get(j) == pivot) {
                    swap(list, --q, j);
                }
            }
            // 交换等于的区间到中间
            // 循环终止时，j处的数必定小于pivot，因为如果等于pivot会和小于pivot的数交换
            // 故此时(p, j]区间小于pivot [j+1, q)区间大于pivot
            i = j + 1;
            var left = j - p + lo;
            var right = hi - q + 1 + j;
            if (left <= index && right >= index) {
                return pivot;
            }
            for (int k = lo; k <= p; k++) {
                swap(list, k, j--);
            }
            for (int k = hi; k >= q; k--) {
                swap(list, k, i++);
            }
            // [lo, j]小于 [i, hi]大于
            if (index <= j) {
                hi = j;
            } else if (index >= i) {
                lo = i;
            }
        }
    }

    //==============================================================================================

}
