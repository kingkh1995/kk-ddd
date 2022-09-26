package com.kk.ddd.support.messaging.sql;

import com.kk.ddd.support.messaging.MessageStorageSQLProperties;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class H2MessageStorageSQLProperties implements MessageStorageSQLProperties {

  @Override
  public String insertForMessageLog() {
    return null;
  }
}
