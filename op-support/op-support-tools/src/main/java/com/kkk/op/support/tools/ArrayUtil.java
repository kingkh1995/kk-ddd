package com.kkk.op.support.tools;

/**
 * todo... 转为Comparable 集合查找
 *
 * @author KaiKoo
 */
public final class ArrayUtil {

    private ArrayUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

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

    /**
     * 斐波那契查找 效率与二分查找一致
     * @param arr
     * @param key
     * @return
     */
    public static int fbSearch(int[] arr, int key) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        var length = arr.length;
        if (length == 1) {
            return arr[0] == key ? 0 : -1;
        }
        var k = getFbIndex(length) - 1;
        var lo = 0;
        var hi = length - 1;
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

}
