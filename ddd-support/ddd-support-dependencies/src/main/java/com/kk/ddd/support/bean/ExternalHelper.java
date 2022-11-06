package com.kk.ddd.support.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.kk.ddd.support.exception.BusinessException;
import com.kk.ddd.support.exception.ExternalServerException;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.validation.constraints.NotNull;

/**
 * 供external facade使用 <br>
 *
 * @author KaiKoo
 */
public class ExternalHelper {

  private ExternalHelper() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static <T> T unwrapResult(@NotNull Result<T> result) throws BusinessException {
    if (result.isSucceeded()) {
      return result.getData();
    }
    throw new BusinessException(result.getCode(), result.getMessage());
  }

  public static <T> T unwrap(@NotNull Supplier<Result<T>> resultSupplier) {
    try {
      return unwrapResult(resultSupplier.get());
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new ExternalServerException(e);
    }
  }

  public static <T> T unwrap(
      @NotNull Supplier<String> jsonSupplier,
      @NotNull Function<JsonNode, Result<T>> toResultFunction) {
    try {
      return unwrapResult(toResultFunction.apply(Kson.readJson(jsonSupplier.get())));
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new ExternalServerException(e);
    }
  }
}
