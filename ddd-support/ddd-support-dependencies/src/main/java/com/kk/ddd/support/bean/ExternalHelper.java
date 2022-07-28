package com.kk.ddd.support.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.kk.ddd.support.exception.BusinessException;
import com.kk.ddd.support.exception.ExternalServerException;
import java.util.function.Function;
import java.util.function.Predicate;
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

  public static <T> T unwrapResult(@NotNull Result<T> result) throws BusinessException {
    if (result.isSucceeded()) {
      return result.getData();
    }
    throw new BusinessException(result.getCode(), result.getMessage());
  }

  public static <T> T unwrapResultSupplier(@NotNull Supplier<Result<T>> supplier) {
    try {
      return unwrapResult(supplier.get());
    } catch (BusinessException e) {
      throw e;
    } catch (Throwable e) {
      throw new ExternalServerException(e);
    }
  }

  /**
   * unwrap json
   *
   * @param json json字符串
   * @param codeFinder code查找函数
   * @param codePredicate 通过code判断是否执行成功
   * @param dataFinder data查找函数
   * @return data
   * @param <T> data类型
   * @throws BusinessException 如果执行失败则抛出
   */
  public static <T> T unwrapJson(
      String json,
      Function<JsonNode, String> codeFinder,
      Predicate<String> codePredicate,
      Function<JsonNode, T> dataFinder)
      throws BusinessException {
    var jsonNode = Kson.readJson(json);
    var code = codeFinder.apply(jsonNode);
    if (codePredicate.test(code)) {
      return dataFinder.apply(jsonNode);
    }
    throw new BusinessException(code, json);
  }

  public static <T> T unwrapJsonSupplier(
      Supplier<String> supplier,
      Function<JsonNode, String> codeFinder,
      Predicate<String> codePredicate,
      Function<JsonNode, T> dataFinder) {
    try {
      return unwrapJson(supplier.get(), codeFinder, codePredicate, dataFinder);
    } catch (BusinessException e) {
      throw e;
    } catch (Throwable e) {
      throw new ExternalServerException(e);
    }
  }
}
