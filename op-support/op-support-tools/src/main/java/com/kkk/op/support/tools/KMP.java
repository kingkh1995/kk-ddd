package com.kkk.op.support.tools;

import java.io.Reader;
import java.util.Arrays;

/**
 * KMP算法查找字符串 <br>
 * fixme... 有bug，待实现
 *
 * @author KaiKoo
 */
public class KMP {

  /** 辅助数组 */
  private final int[] aux;

  private final char[] arr;

  private KMP(char[] arr) {
    this.arr = arr;
    // 构造辅助数组
    var len = this.arr.length;
    this.aux = new int[len];
    this.aux[0] = -1;
    var k = -1;
    var j = 0;
    while (j < len - 1) {
      if (k == -1 || this.arr[j] == this.arr[k]) {
        k++;
        j++;
        this.aux[j] = k;
      } else {
        k = this.aux[k];
      }
    }
  }

  public static KMP compile(String template) {
    if (template == null || template.isBlank()) {
      throw new IllegalArgumentException("template can not be blank!");
    }
    return compile0(template.toCharArray());
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

  public int indexOf(String str) {
    if (str == null || str.isBlank()) {
      return -1;
    }
    var len = this.arr.length;
    var sLen = str.length();
    var i = 0;
    var j = 0;
    while (i < sLen && j < len) {
      if (j == -1 || str.charAt(i) == this.arr[j]) {
        i++;
        j++;
        if (j == len) {
          return i - j;
        }
      } else {
        j = this.aux[j];
      }
    }
    return -1;
  }

  // todo... 从字符流中读取，并查找出匹配的所有index
  public long[] search(Reader reader) {
    return null;
  }
}
