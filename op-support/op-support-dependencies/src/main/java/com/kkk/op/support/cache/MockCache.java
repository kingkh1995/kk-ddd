package com.kkk.op.support.cache;

import com.kkk.op.support.annotation.DegradedService;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.marker.Cache;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import org.springframework.util.Assert;

/**
 * <br>
 *
 * @author KaiKoo
 */
@DegradedService(callbackClass = DegradedMockCache.class)
@AllArgsConstructor
public class MockCache implements Cache {

  private final String name;

  @Override
  public String getName() {
    Assert.hasText(this.name, "Is empty!");
    return this.name;
  }

  @Override
  public <T> Optional<ValueWrapper<T>> get(String key, Class<T> type) {
    if (ThreadLocalRandom.current().nextBoolean()) {
      if (ThreadLocalRandom.current().nextBoolean()) {
        throw new RuntimeException("未知异常！");
      } else {
        throw new BusinessException("业务异常！");
      }
    }
    return Optional.empty();
  }

  @Override
  public <T> Optional<T> get(String key, Class<T> type, Callable<T> loader) {
    return Optional.empty();
  }

  @Override
  public void put(String key, Object obj) {}

  @Override
  public void evict(String key) {}

  @Override
  public void clear() {}

  private void health() {
    if (ThreadLocalRandom.current().nextBoolean()) {
      throw new RuntimeException("心跳失败！");
    }
  }
}
