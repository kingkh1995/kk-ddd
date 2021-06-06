package com.kkk.op.support.marker;

import com.kkk.op.support.function.Worker;
import javax.validation.constraints.NotBlank;

/**
 * todo... 待设计 & 服务降级
 *
 * @author KaiKoo
 */
public interface Cache<T> {

    boolean put(@NotBlank String key, T t);

    T get(@NotBlank String key);

    boolean remove(@NotBlank String key);

    // todo... 缓存双删 通过EventBus发送消息?
    default void doubleRemove(@NotBlank String key, Worker worker) {
        this.remove(key);
        worker.work();
        // 延迟删除
    }

}
