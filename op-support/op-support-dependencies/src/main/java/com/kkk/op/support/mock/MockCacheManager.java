package com.kkk.op.support.mock;

import com.kkk.op.support.marker.CacheManager;

public class MockCacheManager implements CacheManager {

  @Override
  public void put(String key, Object obj) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object get(String key, Class<?> clazz) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(String key) {
    throw new UnsupportedOperationException();
  }
}
