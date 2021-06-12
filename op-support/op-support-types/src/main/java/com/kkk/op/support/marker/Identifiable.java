package com.kkk.op.support.marker;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface Identifiable<ID extends Identifier> {

  ID getId();

  /** insert之后填补id */
  void fillInId(ID id);
}
