package com.kkk.op.user.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * account
 *
 * @author KaiKoo
 */
@Data
@TableName("account")
public class AccountDO {

    @TableId(type = IdType.ASSIGN_ID)//默认实现雪花算法
    private Long id;

    private Long userId;

    private String status;

    //todo... 乐观锁及逻辑删除

}
