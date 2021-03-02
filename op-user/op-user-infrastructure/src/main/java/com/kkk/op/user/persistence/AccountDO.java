package com.kkk.op.user.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * account
 *
 * @author KaiKoo
 */
@Data
public class AccountDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
}
