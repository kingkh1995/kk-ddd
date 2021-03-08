package com.kkk.op.support.marker;

/**
 * Entity：拥有唯一标识和业务行为，尽可能的由DP组成
 * 实体类 marker 接口
 * @author KaiKoo
 */
public interface Entity<ID extends Identifier> extends Identifiable<ID> {

    /**
     * 获取快照
     * @return
     */
    Object snapshot();

}

