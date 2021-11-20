package com.kkk.op.support.marker;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 缓存，锁等名称生成器 <br>
 *
 * @author KaiKoo
 */
@FunctionalInterface
public interface NameGenerator {

  String generate(String... paths);

  NameGenerator DEFAULT = joiner("#", "", "");

  static NameGenerator joiner(String delimiter, String prefix, String suffix) {
    return paths ->
        Arrays.stream(paths)
            .filter(Objects::nonNull)
            .filter(Predicate.not(String::isBlank))
            .collect(Collectors.joining(delimiter, prefix, suffix));
  }
}
