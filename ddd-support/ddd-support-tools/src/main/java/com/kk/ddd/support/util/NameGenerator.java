package com.kk.ddd.support.util;

/**
 * 缓存，锁等名称生成器 <br>
 *
 * @author KaiKoo
 */
@FunctionalInterface
public interface NameGenerator {

  String generate(CharSequence... paths);

  NameGenerator DEFAULT = joiner("@", "", "");

  static NameGenerator joiner(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
    return paths -> prefix + String.join(delimiter, paths) + suffix;
  }
}
