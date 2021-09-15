package com.kkk.op.support.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;

/**
 * json工具类 <br>
 * todo... 待完善，添加@ConditionalOnBean注解配合JsonMapper
 *
 * @author KaiKoo
 */
@RequiredArgsConstructor
public final class Kson {

  private final JsonMapper jsonMapper;

  public String writeJson(Object value) {
    try {
      return this.jsonMapper.writeValueAsString(value);
    } catch (Exception e) {
      throw new KsonExceptiion(e);
    }
  }

  public <T> T readJson(String content, Class<T> type) {
    try {
      return this.jsonMapper.readValue(content, type);
    } catch (Exception e) {
      throw new KsonExceptiion(e);
    }
  }

  // 带泛型情况下使用
  public <T> T readJson(String content, TypeReference<T> typeReference) {
    try {
      return this.jsonMapper.readValue(content, typeReference);
    } catch (Exception e) {
      throw new KsonExceptiion(e);
    }
  }

  // 不确定类型或只需要部分解析情况下使用
  public JsonNode readJson(String content) {
    try {
      return this.jsonMapper.readTree(content);
    } catch (Exception e) {
      throw new KsonExceptiion(e);
    }
  }

  public class KsonExceptiion extends RuntimeException {
    KsonExceptiion(Throwable cause) {
      super(cause);
    }
  }
}
