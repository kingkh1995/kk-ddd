package com.kk.ddd.user.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * user <br>
 * PO类字段和字段类型要求与数据库完全一致，且应该是record。 <br>
 * todo... user_base & user_security
 *
 * @author KaiKoo
 */
@Data
public class UserPO implements Serializable {

  /**
   * 主键ID <br/>
   * todo... 分布式ID生成
   */
  private Long id;

  /**
   * 用户状态 <br/>
   * @see com.kk.ddd.support.enums.UserStateEnum
   * state用来标识可迁移的状态（如state machine），status用来表示不可迁移的状态（如Http status code） <br/>
   */
  private String state;

  /**
   * 用户名
   */
  private String name;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 邮箱
   */
  private String email;

  /**
   * 创建时间 <br/>
   * 根据阿里巴巴Java开发手册，日期类型应该使用DATETIME和java.util.Date。
   */
  private Date createTime;

  /**
   * 更新时间 <br/>
   */
  private Date updateTime;

  /**
   * 乐观锁版本号
   */
  private Integer version;

  /**
   * 用户绑定账号 <br/>
   * OneToMany
   */
  private List<AccountPO> accounts;
}
