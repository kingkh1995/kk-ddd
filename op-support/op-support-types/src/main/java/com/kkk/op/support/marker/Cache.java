package com.kkk.op.support.marker;

/**
 * @author KaiKoo
 */
public interface Cache<T> {

    boolean put(String key, T t);

    T get(String key);

    boolean remove(String key);

}
