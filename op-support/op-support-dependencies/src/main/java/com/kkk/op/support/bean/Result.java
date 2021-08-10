package com.kkk.op.support.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回参数 <br>
 *
 * @author KaiKoo
 */
public class Result<T> implements Serializable {

  private static final int SUCCESS_CODE = 0;

  private static final int FAIL_CODE = 1;

  private static final String SUCCESS_MESSAGE = "ok";

  private int code;

  private String message;

  private T data;

  /** 额外信息：url，traceId，timestamp等 */
  private Map<String, Object> extend;

  private Result(int code, String message) {
    this.code = code;
    this.message = message;
  }

  private Result(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static Result<?> success() {
    return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE);
  }

  public static Result<?> fail(String message) {
    return new Result<>(FAIL_CODE, message);
  }

  public static Result<?> fail(int code, String message) {
    return new Result<>(code, message);
  }

  public static <T> Result<T> success(T t) {
    return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, t);
  }

  private Map<String, Object> getExtend() {
    if (this.extend == null) {
      this.extend = new HashMap<>();
    }
    return this.extend;
  }

  public void addExtend(String key, Object obj) {
    this.getExtend().put(key, obj);
  }

  public void addExtend(Map<String, Object> map) {
    this.getExtend().putAll(map);
  }
}
