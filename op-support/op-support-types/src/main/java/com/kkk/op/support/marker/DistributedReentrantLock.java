package com.kkk.op.support.marker;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author KaiKoo
 */
public interface DistributedReentrantLock {

    /**
     * 获取锁，失败不重试
     */
    void tryLock(@NotBlank String key);

    /**
     * 获取锁，失败则自旋重试
     */
    void tryLock(@NotBlank String key, int retry);

    /**
     * 释放锁（不抛出异常）
     */
    void unlock(@NotBlank String key);
}
