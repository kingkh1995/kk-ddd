package com.kkk.op.support.markers;

/**
 * Entity：拥有唯一标识和业务行为，尽可能的由Types组成
 * 实体类 marker 接口
 * @author KaiKoo
 */
public interface Entity<ID extends Identifier> extends Identifiable<ID> {

    /**
     * 获取快照
     * @return
     */
    Entity snapshot();

}
