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
    addExtend();
    this.code = code;
    this.message = message;
  }

  // todo... 无论成功与否均添加返回值信息，暂时先放到这里，后序加到ResponseBodyAdvice类中
  private void addExtend() {
    var requestContext = LocalRequestContextHolder.getLocalRequestContext();
    if (requestContext != null) {
      this.extend = new HashMap<>();
      this.extend.put("costTime", requestContext.calculateCostMillis() + "ms");
      this.extend.put("traceId", requestContext.getTraceId());
    }
  }

  private Result(int code, String message, T data) {
    this(code, message);
    this.data = data;
  }

  public static <T> Result<T> success() {
    return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE);
  }

  public static <T> Result<T> success(T t) {
    return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, t);
  }

  public static <T> Result<T> fail(String message) {
    return new Result<>(FAIL_CODE, message);
  }

  public static <T> Result<T> fail(int code, String message) {
    return new Result<>(code, message);
  }
}
