package com.kkk.op.support.constant;

/**
 * type constants spi provider <br>
 *
 * @author KaiKoo
 */
public interface TypeConstantsProvider {

  default long defaultPageSize() {
    return 10L;
  }

  default long maximumPageSize() {
    return 500L;
  }

  default int versionCacheHigh() {
    return 99;
  }
}
