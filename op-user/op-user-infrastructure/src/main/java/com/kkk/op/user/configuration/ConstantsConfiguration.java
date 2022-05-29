package com.kkk.op.user.configuration;

import com.kkk.op.support.constant.TypeConstantsProvider;

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
