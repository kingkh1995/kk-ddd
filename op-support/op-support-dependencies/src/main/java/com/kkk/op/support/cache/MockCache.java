package com.kkk.op.support.cache;

import com.kkk.op.support.marker.Cache;
import com.kkk.op.support.marker.ValueWrapper;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.util.Assert;

/**
 * <br>
 *
 * @author KaiKoo
 */
@AllArgsConstructor
public class MockCache implements Cache {

  private final String name;

  @Override
  public String getName() {
    Assert.hasText(this.name, "Is empty!");
    return this.name;
  }

  @Override
  public <T> Optional<ValueWrapper<T>> get(String key, Class<T> clazz) {
    return Optional.empty();
  }

  @Override
  public void put(String key, Object obj) {}

  @Override
  public void evict(String key) {}

  @Override
  public void clear() {}
}
