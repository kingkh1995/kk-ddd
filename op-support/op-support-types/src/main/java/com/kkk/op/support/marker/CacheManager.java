package com.kkk.op.support.marker;

/**
 * @author KaiKoo
 */
public interface CacheManager<T> {

    boolean cachePut(String key, T t);

    T cacheGet(String key);

    boolean cacheRemove(String key);

}
