package com.kkk.op.support.marker;

import java.util.Collection;
import java.util.List;

/**
 * DataConverter marker接口
 * @author KaiKoo
 */
public interface DataConverter<T extends Entity, P> {

    P toData(T t);

    T fromData(P data);

    List<P> toData(Collection<T> entityCol);

    List<T> fromData(Collection<P> dataCol);

}
