package com.kk.ddd.support.messaging.sql;

import com.kk.ddd.support.messaging.MessageStorageSQLProperties;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
public class MySQLMessageStorageSQLProperties implements MessageStorageSQLProperties {

    @Override
    public String insertForMessageLog() {
        return null;
    }
}
