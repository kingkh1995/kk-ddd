package com.kk.ddd.support.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.kk.ddd.support.util.Constants;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Getter
public class Result<T> implements Serializable {

  private static final String SUCCEED_CODE = Constants.BASE.succeedCode();

  private static final String SUCCEED_MESSAGE = Constants.BASE.succeedMessage();

  private static final String FAIL_CODE = Constants.BASE.failCode();

  private final String code;

  private final String message;

  private T data;

  /** 额外字段：url，traceId，timestamp等 */
  @JsonAnyGetter // 其他额外字段序列化为属性
  private Map<String, Object> attrs;

  private Result(String code, String message) {
    this.code = code;
    this.message = message;
  }

  private Result(String code, String message, T data) {
    this(code, message);
    this.data = data;
  }

  public boolean isSucceeded() {
    return SUCCEED_CODE.equals(this.code);
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

  public static <T> Result<T> fail(String code, String message) {
    return new Result<>(code, message);
  }
}
