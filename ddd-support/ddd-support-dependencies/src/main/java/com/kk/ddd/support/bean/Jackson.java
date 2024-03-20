package com.kk.ddd.support.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.kk.ddd.support.json.JacksonException;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * json工具类 <br>
 *
 * @author KaiKoo
 */
public final class Jackson {

  private static volatile JsonMapper MAPPER;

  private Jackson() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  // 静态域懒加载使用lazy initialization holder class idiom模式，首次调用时静态内部类才会初始化。
  private static class JsonMapperHolder {
    private static final JsonMapper INSTANCE = newJsonMapperBuilder().build();
  }

  private static JsonMapper getMapper() {
    return Objects.isNull(MAPPER) ? JsonMapperHolder.INSTANCE : MAPPER;
  }

  public static void setMapper(@NotNull JsonMapper jsonMapper) {
    MAPPER = Objects.requireNonNull(jsonMapper);
  }

  public static JsonMapper.Builder newJsonMapperBuilder() {
    return JsonMapper.builder()
        // 自动注册模块（jdk8Time类：JavaTimeModule）
        .findAndAddModules()
        // 序列化时只按属性
        .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        // 反序列化时忽略多余字段不失败
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        // 序列化时空对象不失败
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        // 序列化时日期不转为时间戳
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        // BigDecimal按plain方式序列化
        .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        // 禁用类型功能，防止反序列化安全问题。
        .deactivateDefaultTyping();
  }

  public static String object2String(@NotNull Object value) {
    try {
      return getMapper().writeValueAsString(value);
    } catch (Exception e) {
      throw new JacksonException(e);
    }
  }

  public static byte[] object2Bytes(@NotNull Object value) {
    try {
      return getMapper().writeValueAsBytes(value);
    } catch (Exception e) {
      throw new JacksonException(e);
    }
  }

  public static <T> T parse(String content, @NotNull Class<T> type) {
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
  public static <T> T parse(String content, @NotNull TypeReference<T> typeRef) {
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
  public static JsonNode parse(String content) {
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
  public static <T> T convert(Object source, @NotNull Class<T> targetType) {
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
  public static <T> T convert(Object source, @NotNull TypeReference<T> targetTypeRef) {
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
