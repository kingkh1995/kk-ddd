package com.kkk.op.support.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Objects;

/**
 * json工具类 <br>
 * todo... 待完善
 *
 * @author KaiKoo
 */
public final class Uson {
  private final JsonMapper jsonMapper;

  public Uson(JsonMapper jsonMapper) {
    this.jsonMapper = Objects.requireNonNull(jsonMapper);
  }

  public String toJson(Object value) {
    try {
      return this.jsonMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
