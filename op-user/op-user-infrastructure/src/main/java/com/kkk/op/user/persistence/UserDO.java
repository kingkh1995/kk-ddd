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

    //    @TableId(type = IdType.ASSIGN_ID)//默认雪花算法实现
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String username;

    private String password;

    private String gender;

    private Byte age;

    private String email;

}
