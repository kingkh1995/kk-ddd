package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.ValidateUtils;
import com.kk.ddd.user.domain.type.Hash.HashBuilder;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * todo... 使用jdk spi注入静态属性 <br>
 *
 * @author KaiKoo
 */
@JsonDeserialize(builder = HashBuilder.class)
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Hash implements Type {

  // 支持的哈希算法
  public static final Set<String> ALGORITHM_SUPPORTED = Set.of("MD5", "SHA-256");

  // 盐值最大长度
  public static final int SALT_MAX_LENGTH = 100;

  // 最大加盐次数
  public static final int MAX_ITERATIONS = 3;

  private final String algorithm;
  private final String salt;
  private final int iterations;

  private static Hash of(
      final String algorithm, final String salt, final int iterations, final String fieldName) {
    if (!ALGORITHM_SUPPORTED.contains(algorithm)) {
      throw new IllegalArgumentException(fieldName + "不支持此哈希算法");
    }
    ValidateUtils.nonBlank(salt, fieldName + "盐值");
    ValidateUtils.maxLength(salt, SALT_MAX_LENGTH, true, fieldName + "盐值");
    ValidateUtils.minValue(iterations, 0, false, fieldName + "加盐次数");
    ValidateUtils.maxValue(iterations, MAX_ITERATIONS, true, fieldName + "加盐次数");
    return new Hash(algorithm, salt, iterations);
  }

  @Builder
  public static Hash of(final String algorithm, final String salt, final int iterations) {
    return of(algorithm, salt, iterations, "Hash");
  }

  public static Hash valueOf(
      final String algorithm, final String salt, final int iterations, final String fieldName) {
    return of(algorithm, salt, iterations, fieldName);
  }
}
