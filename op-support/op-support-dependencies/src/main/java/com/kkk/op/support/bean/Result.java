package com.kkk.op.support.bean;

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

  /** 额外信息：url，traceId，timestamp等 */
  private Map<String, Object> adds;

  private Result(int code, String message) {
    this.code = code;
    this.message = message;
  }

  private Result(int code, String message, T data) {
    this(code, message);
    this.data = data;
  }

  public void append(String key, Object value) {
    if (this.adds == null) {
      this.adds = new LinkedHashMap<>();
    }
    this.adds.put(key, value);
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
