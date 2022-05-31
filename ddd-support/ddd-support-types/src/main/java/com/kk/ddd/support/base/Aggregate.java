package com.kk.ddd.support.base;

import com.kk.ddd.support.marker.Identifier;

/**
 * Aggregate：Entity的聚合 <br>
 * 聚合根类 marker
 *
 * @author KaiKoo
 */
public abstract class Aggregate<ID extends Identifier> extends Entity<ID> {}
