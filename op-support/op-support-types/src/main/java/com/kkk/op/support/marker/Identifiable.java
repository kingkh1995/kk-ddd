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

  default boolean isIdentified() {
    return this.getId() != null;
  }

  static boolean isIdentified(Identifiable<?> identifiable) {
    return identifiable != null && identifiable.isIdentified();
  }
}
