package com.kkk.op.support.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class Result<T> implements Serializable {

  private static final int SUCCEED_CODE = 0;

  private static final int FAIL_CODE = 1;

  private static final String SUCCEED_MESSAGE = "ok";

  private int code;

  private String message;

  private T data;

  /** 额外字段：url，traceId，timestamp等 */
  @JsonAnyGetter // 其他额外字段序列化为属性
  private Map<String, Object> attrs;

  private Result(int code, String message) {
    this.code = code;
    this.message = message;
  }

  private Result(int code, String message, T data) {
    this(code, message);
    this.data = data;
  }

  @JsonAnySetter // 反序列化时未知属性全部添加到attrs中
  public void append(String key, Object value) {
    if (this.attrs == null) {
      this.attrs = new LinkedHashMap<>();
    }
    this.attrs.put(key, value);
  }

  public static <T> Result<T> succeed() {
    return new Result<>(SUCCEED_CODE, SUCCEED_MESSAGE);
  }

  public static <T> Result<T> succeed(T t) {
    return new Result<>(SUCCEED_CODE, SUCCEED_MESSAGE, t);
  }

  public static <T> Result<T> fail(String message) {
    return new Result<>(FAIL_CODE, message);
  }

  public static <T> Result<T> fail(int code, String message) {
    return new Result<>(code, message);
  }
}
