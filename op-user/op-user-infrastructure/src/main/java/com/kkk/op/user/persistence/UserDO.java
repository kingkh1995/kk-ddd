package com.kkk.op.user.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * user
 *
 * @author KaiKoo
 */
@Data
@TableName("user")
public class UserDO {

    @TableId(type = IdType.ASSIGN_ID) //默认实现雪花算法
    private Long id;

    private String name;

    private String username;

    private String password;

    private String gender;

    private Byte age;

    private String email;

}
