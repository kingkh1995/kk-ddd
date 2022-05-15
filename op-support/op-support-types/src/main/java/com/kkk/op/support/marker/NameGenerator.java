package com.kkk.op.support.marker;

import java.util.Arrays;
import java.util.stream.Collectors;

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
    return paths -> Arrays.stream(paths).collect(Collectors.joining(delimiter, prefix, suffix));
  }
}
