package com.kk.ddd.user.configuration;

import com.kk.ddd.support.util.spi.TypeConstantsProvider;

/**
 * 自定义spi拓展点 <br>
 *
 * @author KaiKoo
 */
public class ConstantsConfiguration implements TypeConstantsProvider {

  @Override
  public long defaultPageSize() {
    return 20L;
  }

  @Override
  public long maximumPageSize() {
    return 1000L;
  }
}
