package com.kkk.op.support.tracking.diff;

import java.io.Serializable;
import java.util.function.Function;

/**
 * <br>
 *
 * @author KaiKoo
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {}