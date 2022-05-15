package com.kkk.op.support.constant;

/**
 * type constants spi provider <br>
 *
 * @author KaiKoo
 */
public interface TypeConstantsProvider {

  default long getDefaultPageSize() {
    return 10L;
  }

  default long getMaxPageSize() {
    return 500L;
  }

  default int getVersionCacheEnd() {
    return 128;
  }
}
