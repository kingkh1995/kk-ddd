package com.kkk.op.support.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Objects;

/**
 * json工具类 <br>
 * todo... 待完善，添加@ConditionalOnBean注解配合JsonMapper
 *
 * @author KaiKoo
 */
public final class Uson {

  private final JsonMapper jsonMapper;

  public Uson(JsonMapper jsonMapper) {
    this.jsonMapper = Objects.requireNonNull(jsonMapper);
  }

  public String writeJson(Object value) {
    try {
      return this.jsonMapper.writeValueAsString(value);
    } catch (Exception e) {
      throw new UsonExceptiion(e);
    }
  }

  public <T> T readJson(String content, TypeReference<T> typeReference) {
    try {
      return this.jsonMapper.readValue(content, typeReference);
    } catch (Exception e) {
      throw new UsonExceptiion(e);
    }
  }

  public JsonNode readJson(String content) {
    try {
      return this.jsonMapper.readTree(content);
    } catch (Exception e) {
      throw new UsonExceptiion(e);
    }
  }

  public class UsonExceptiion extends RuntimeException {
    public UsonExceptiion(Throwable cause) {
      super(cause);
    }
  }
}
