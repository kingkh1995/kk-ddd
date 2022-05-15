package com.kkk.op.user.configuration;

import com.kkk.op.support.constant.TypeConstantsProvider;

/**
 * 自定义spi拓展点 <br>
 *
 * @author KaiKoo
 */
public class BaseConstantsConfiguration implements TypeConstantsProvider {

  @Override
  public long getDefaultPageSize() {
    return 20L;
  }

  @Override
  public long getMaxPageSize() {
    return 1000L;
  }
}
