package com.kkk.op.support.util;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Boyer-Moore算法查找子字符串 <br>
 *
 * @author KaiKoo
 */
public class BoyerMoore {

  private final HashMap<Character, Integer> right;

  private final char[] arr;

  private BoyerMoore(char[] arr) {
    this.arr = arr;
    this.right = new HashMap<>(arr.length);
    // 记录字符在模式中的最右位置
    for (int i = 0; i < arr.length; i++) {
      right.put(arr[i], i);
    }
  }

  private int getRight(char c) {
    return this.right.getOrDefault(c, -1);
  }

  public static BoyerMoore compile(String pattern) {
    if (pattern == null || pattern.isBlank()) {
      throw new IllegalArgumentException("pattern can not be blank!");
    }
    return compile0(pattern.toCharArray());
  }

  public static BoyerMoore compile(char[] chars) {
    if (chars == null || chars.length == 0) {
      throw new IllegalArgumentException("chars can not be empty!");
    }
    return compile0(Arrays.copyOf(chars, chars.length));
  }

  private static BoyerMoore compile0(char[] arr) {
    return new BoyerMoore(arr);
  }

  public int indexOf(String text) {
    if (text == null || text.isBlank()) {
      return -1;
    }
    for (int i = 0, skip; i <= text.length() - arr.length; i += skip) {
      // 每轮扫描开始将文本指针移动的距离初始化为0
      skip = 0;
      // 模式指针从右往左扫描，每轮开始时均重置到最右端。
      for (int j = arr.length - 1; j >= 0; j--) {
        // 不匹配则结束当前一轮扫描并将文本指针右移对齐。
        var c = text.charAt(i + j);
        if (arr[j] != c) {
          // 计算需要右移动的距离，且文本指针只能右移。
          skip = Math.max(1, j - getRight(c));
          // 结束循环
          break;
        }
      }
      // 文本指针不需要移动则表示匹配到结果
      if (skip == 0) {
        return i;
      }
    }
    // 未找到匹配
    return -1;
  }
}
