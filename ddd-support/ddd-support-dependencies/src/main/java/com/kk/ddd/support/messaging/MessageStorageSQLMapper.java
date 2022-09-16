package com.kk.ddd.support.messaging;

import java.sql.SQLException;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * todo...
 * <br/>
 *
 * @author KaiKoo
 */
public class MessageStorageSQLMapper {

    private static final String MAPPER_PACKAGE = MessageStorageSQLMapper.class.getPackageName() + ".sql";

    private static final String SQL_MAPPER_SUFFIX = "MessageStorageSQLProperties";

    private final JdbcTemplate jdbcTemplate;

    private final MessageStorageSQLProperties sqlProperties;

    public MessageStorageSQLMapper(final JdbcTemplate jdbcTemplate) throws SQLException, ReflectiveOperationException {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        try (var connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            var databaseProductName = connection.getMetaData().getDatabaseProductName();
            var sqlPropertiesClassName = MAPPER_PACKAGE + "." + databaseProductName + SQL_MAPPER_SUFFIX;
            this.sqlProperties = (MessageStorageSQLProperties) Class.forName(sqlPropertiesClassName).getDeclaredConstructor().newInstance();
        }
    }

    public void createTableIfNotExisted() {

    }

    public void insert(MessageModel model) {
        var result = jdbcTemplate.update(sqlProperties.insertForMessageLog(), model.getFormerId(),
                model.getTopic(), model.getHashKey(), model.getCreateTime(), model.getHeader(), model.getPayload());
        if (result == 0) {
            throw new IllegalStateException("insertForMessageLog failed");
        }
    }

    public void updateForComplete(Long modelId) {
    }
}
