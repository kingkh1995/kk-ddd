package com.kkk.op.support.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * json工具类 <br>
 *
 * @author KaiKoo
 */
@Validated
@RequiredArgsConstructor
public class Kson {

  private final JsonMapper jsonMapper;

  public ObjectMapper getMapper() {
    return jsonMapper.copy();
  }

  public String writeJson(Object value) {
    try {
      return this.jsonMapper.writeValueAsString(value);
    } catch (Exception e) {
      throw new KsonException(e);
    }
  }

  public <T> T readJson(@NotNull String content, @NotNull Class<T> type) {
    try {
      return this.jsonMapper.readValue(content, type);
    } catch (Exception e) {
      throw new KsonException(e);
    }
  }

  // 带泛型情况下使用
  public <T> T readJson(@NotNull String content, @NotNull TypeReference<T> typeRef) {
    try {
      return this.jsonMapper.readValue(content, typeRef);
    } catch (Exception e) {
      throw new KsonException(e);
    }
  }

  // 类型未知或只需要部分解析情况下使用
  public JsonNode readJson(@NotNull String content) {
    try {
      return this.jsonMapper.readTree(content);
    } catch (Exception e) {
      throw new KsonException(e);
    }
  }

  // 对象深拷贝
  public <T> T convertValue(@NotNull Object source, @NotNull Class<T> targetType) {
    return this.jsonMapper.convertValue(source, targetType);
  }

  // 对象深拷贝 带泛型情况下使用
  public <T> T convertValue(@NotNull Object source, @NotNull TypeReference<T> targetTypeRef) {
    return this.jsonMapper.convertValue(source, targetTypeRef);
  }

  public static class KsonException extends RuntimeException {
    KsonException(Throwable cause) {
      super(cause);
    }
  }
}
