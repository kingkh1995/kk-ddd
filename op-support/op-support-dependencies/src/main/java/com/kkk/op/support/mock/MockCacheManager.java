package com.kkk.op.support.mock;

import com.kkk.op.support.marker.CacheManager;
import java.util.Optional;

public class MockCacheManager implements CacheManager {

  @Override
  public void put(String key, Object obj) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String get(String key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(String key) {
    throw new UnsupportedOperationException();
  }
}
