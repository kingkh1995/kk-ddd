package com.kk.ddd.support.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;

/**
 * 领域事件
 * <br/>
 * todo... 待设计
 *
 * @author KaiKoo
 */
@JsonDeserialize(builder = EntityEvent.EntityEventBuilder.class)
@Getter
@Builder
public class EntityEvent<T extends Entity<ID>, ID extends Identifier> {
    String type;
    T entity;
}
