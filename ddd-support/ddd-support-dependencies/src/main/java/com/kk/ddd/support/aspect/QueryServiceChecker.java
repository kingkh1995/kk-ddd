package com.kk.ddd.support.aspect;

/**
 * <br>
 *
 * @author kingk
 */
public interface QueryServiceChecker {
    boolean checkBefore(Object target, Object[] args);

    boolean checkAfter(Object target, Object Result);
}
