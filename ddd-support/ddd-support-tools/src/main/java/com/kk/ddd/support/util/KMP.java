package com.kk.ddd.support.util;

import java.util.Arrays;

/**
 * KMP算法 <br>
 *
 * @author KaiKoo
 */
public class KMP {

  // 模式字符串
  private final char[] arr;
  // 辅助数组，表示模式指针在当前位置不匹配时的回退位置，因为当前子字符串的前缀和后缀有相同部分，则可以直接回退到前缀位置。
  private final int[] aux;

  private KMP(char[] arr) {
    this.arr = arr;
    var length = arr.length;
    aux = new int[length];
    aux[0] = -1;
    if (length == 1) {
      return;
    }
    for (int k, i = 1; i < length; ++i) {
      k = aux[i - 1];
      while (k != -1) {
        if (arr[i - 1] == arr[k]) {
          aux[i] = k + 1;
          break;
        } else {
          k = aux[k];
        }
      }
    }
  }

  public static KMP compile(String pattern) {
    if (pattern == null || pattern.isBlank()) {
      throw new IllegalArgumentException("pattern can not be blank!");
    }
    return compile0(pattern.toCharArray());
  }

  public static KMP compile(char[] chars) {
    if (chars == null || chars.length == 0) {
      throw new IllegalArgumentException("chars can not be empty!");
    }
    return compile0(Arrays.copyOf(chars, chars.length));
  }

  private static KMP compile0(char[] arr) {
    return new KMP(arr);
  }

  public int indexOf(String text) {
    if (text == null || text.isBlank()) {
      return -1;
    }
    var i = 0;
    var j = 0;
    while (i < text.length() && j < arr.length) {
      // 匹配或重启则双指针前进，不匹配则模式指针借助辅助数组回退
      if (j == -1 || text.charAt(i) == arr[j]) {
        i++;
        j++;
      } else {
        j = aux[j];
      }
    }
    // 模式扫描完成表示匹配成功
    return j == arr.length ? i - j : -1;
  }
}
