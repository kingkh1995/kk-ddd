package com.kk.ddd.support.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.kk.ddd.support.exception.JacksonException;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * json工具类 <br>
 *
 * @author KaiKoo
 */
public class Kson {

  private Kson() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  // 静态域懒加载使用lazy initialization holder class idiom模式，首次调用时静态内部类才会初始化。
  private static class JsonMapperMapperHolder {
    private static final JsonMapper INSTANCE =
        JsonMapper.builder()
            .findAndAddModules()
            .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build();
  }

  private static JsonMapper MAPPER;

  public static void setMapper(@NotNull JsonMapper jsonMapper) {
    MAPPER = Objects.requireNonNull(jsonMapper);
  }

  private static JsonMapper getMapper() {
    if (null == MAPPER) {
      MAPPER = JsonMapperMapperHolder.INSTANCE;
    }
    return MAPPER;
  }

  public static String writeJson(Object value) {
    if (value == null) {
      return null;
    }
    try {
      return getMapper().writeValueAsString(value);
    } catch (Exception e) {
      throw new JacksonException(e);
    }
  }

  public static <T> T readJson(String content, @NotNull Class<T> type) {
    if (content == null) {
      return null;
    }
    try {
      return getMapper().readValue(content, type);
    } catch (Exception e) {
      throw new JacksonException(e);
    }
  }

  // 带泛型情况下使用
  public static <T> T readJson(String content, @NotNull TypeReference<T> typeRef) {
    if (content == null) {
      return null;
    }
    try {
      return getMapper().readValue(content, typeRef);
    } catch (Exception e) {
      throw new JacksonException(e);
    }
  }

  // 类型未知或只需要部分解析情况下使用
  public static JsonNode readJson(String content) {
    if (content == null) {
      return NullNode.getInstance();
    }
    try {
      return getMapper().readTree(content);
    } catch (Exception e) {
      throw new JacksonException(e);
    }
  }

  // 对象深拷贝
  public static <T> T convertValue(Object source, @NotNull Class<T> targetType) {
    if (source == null) {
      return null;
    }
    try {
      return getMapper().convertValue(source, targetType);
    } catch (IllegalArgumentException e) {
      throw new JacksonException(e);
    }
  }

  // 对象深拷贝 带泛型情况下使用
  public static <T> T convertValue(Object source, @NotNull TypeReference<T> targetTypeRef) {
    if (source == null) {
      return null;
    }
    try {
      return getMapper().convertValue(source, targetTypeRef);
    } catch (IllegalArgumentException e) {
      throw new JacksonException(e);
    }
  }
}
