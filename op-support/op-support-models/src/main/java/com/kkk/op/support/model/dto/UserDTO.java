package com.kkk.op.support.model.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
public class UserDTO implements Serializable {

  private Long id;

  private String name;

  private String username;

  private String gender;

  private Byte age;

  private String email;

  private List<AccountDTO> accounts;
}
