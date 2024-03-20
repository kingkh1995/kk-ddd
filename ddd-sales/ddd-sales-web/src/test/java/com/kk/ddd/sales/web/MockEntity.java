package com.kk.ddd.sales.web;

import com.kk.ddd.support.core.Entity;
import com.kk.ddd.support.type.LongId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <br>
 *
 * @author kingk
 */
@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class MockEntity extends Entity<LongId> {
  private LongId id;
  private String name;
}
