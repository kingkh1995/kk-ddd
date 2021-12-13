package com.kkk.op.support.bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.util.Assert;

/**
 * json格式类型处理器 <br>
 * json格式属性要求不能为List，为了方便后续升级。
 *
 * @author KaiKoo
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonJacksonTypeHandler extends BaseTypeHandler<Object> {

  private static ObjectMapper OBJECT_MAPPER;

  private final Class<?> type;

  public JsonJacksonTypeHandler(Class<?> type) {
    Assert.notNull(type, "Type argument cannot be null");
    this.type = type;
  }

  public static ObjectMapper getObjectMapper() {
    if (null == OBJECT_MAPPER) {
      OBJECT_MAPPER =
          JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
    }
    return OBJECT_MAPPER;
  }

  public static void setObjectMapper(ObjectMapper objectMapper) {
    Assert.notNull(objectMapper, "ObjectMapper should not be null");
    OBJECT_MAPPER = objectMapper;
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setString(i, toJson(parameter));
  }

  @Override
  public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return parse(rs.getString(columnName));
  }

  @Override
  public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return parse(rs.getString(columnIndex));
  }

  @Override
  public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return parse(cs.getString(columnIndex));
  }

  private Object parse(String json) {
    if (json == null || json.isBlank()) {
      return null;
    }
    try {
      return getObjectMapper().readValue(json, type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String toJson(Object obj) {
    try {
      return getObjectMapper().writeValueAsString(obj);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
