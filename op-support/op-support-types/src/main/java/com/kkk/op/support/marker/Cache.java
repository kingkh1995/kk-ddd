package com.kkk.op.support.marker;

/**
 * todo... 待设计 & 服务降级
 *
 * @author KaiKoo
 */
public interface Cache<T> {

    boolean put(String key, T t);

    T get(String key);

    boolean remove(String key);

}
