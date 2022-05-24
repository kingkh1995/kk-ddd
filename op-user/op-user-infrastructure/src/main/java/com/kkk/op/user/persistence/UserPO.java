package com.kkk.op.user.persistence;

import java.io.Serializable;
import lombok.Data;

/**
 * user
 *
 * @author KaiKoo
 */
@Data
public class UserPO implements Serializable {

  private Long id;

  private String name;

  private String username;

  private String password;

  private String gender;

  private Byte age;

  private String email;
}
