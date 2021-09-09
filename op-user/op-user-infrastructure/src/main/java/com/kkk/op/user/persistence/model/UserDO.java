package com.kkk.op.user.persistence.model;

import lombok.Data;

/**
 * user
 *
 * @author KaiKoo
 */
@Data
public class UserDO {

  private Long id;

  private String name;

  private String username;

  private String password;

  private String gender;

  private Byte age;

  private String email;
}
