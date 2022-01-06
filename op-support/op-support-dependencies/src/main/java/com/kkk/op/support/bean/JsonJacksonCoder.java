package com.kkk.op.support.bean;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.kkk.op.support.exception.JacksonException;
import java.util.Objects;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class JsonJacksonCoder {

  private final JsonMapper jsonMapper;

  public JsonJacksonCoder() {
    this.jsonMapper =
        JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build();
    initTypeInclusion(this.jsonMapper);
  }

  public JsonJacksonCoder(JsonMapper jsonMapper) {
    this.jsonMapper = Objects.requireNonNull(jsonMapper).copy();
    initTypeInclusion(this.jsonMapper);
  }

  public String encode(Object o) {
    if (o == null) {
      return null;
    }
    try {
      return jsonMapper.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new JacksonException(e);
    }
  }

  public Object decode(String json) {
    if (json == null) {
      return null;
    }
    try {
      return jsonMapper.readValue(json, Object.class);
    } catch (JsonProcessingException e) {
      throw new JacksonException(e);
    }
  }

  // copy from JsonJacksonCodec
  protected void initTypeInclusion(ObjectMapper mapObjectMapper) {
    var mapTyper =
        new DefaultTypeResolverBuilder(
            DefaultTyping.NON_FINAL, LaissezFaireSubTypeValidator.instance) {
          public boolean useForType(JavaType t) {
            switch (_appliesFor) {
              case NON_CONCRETE_AND_ARRAYS:
                while (t.isArrayType()) {
                  t = t.getContentType();
                }
                // fall through
              case OBJECT_AND_NON_CONCRETE:
                return (t.getRawClass() == Object.class) || !t.isConcrete();
              case NON_FINAL:
                while (t.isArrayType()) {
                  t = t.getContentType();
                }
                // to fix problem with wrong long to int conversion
                if (t.getRawClass() == Long.class) {
                  return true;
                }
                if (t.getRawClass() == XMLGregorianCalendar.class) {
                  return false;
                }
                return !t.isFinal(); // includes Object.class
              default:
                // case JAVA_LANG_OBJECT:
                return t.getRawClass() == Object.class;
            }
          }
        };
    mapTyper.init(JsonTypeInfo.Id.CLASS, null);
    mapTyper.inclusion(JsonTypeInfo.As.PROPERTY);
    mapObjectMapper.setDefaultTyping(mapTyper);
  }
}
