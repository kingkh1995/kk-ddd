package com.kk.ddd.support.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;

/**
 * 领域事件 <br>
 * 用于修改多个领域时的解耦，在应用层方法的最后发送，同步通知到其他领域。 <br>
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
