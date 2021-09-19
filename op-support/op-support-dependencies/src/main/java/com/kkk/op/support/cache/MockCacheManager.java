package com.kkk.op.support.cache;

import com.kkk.op.support.marker.CacheManager;
import java.util.Optional;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class MockCacheManager implements CacheManager {

  @Override
  public boolean containsKey(String key) {
    return false;
  }

  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    return Optional.empty();
  }

  @Override
  public void put(String key, Object obj) {
    return;
  }

  @Override
  public boolean remove(String key) {
    return true;
  }
}
