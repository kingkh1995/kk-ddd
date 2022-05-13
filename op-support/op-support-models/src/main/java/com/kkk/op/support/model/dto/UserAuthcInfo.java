package com.kkk.op.support.model.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
public class UserAuthcInfo implements Serializable {

  private Long id;

  private String name;

  private String username;

  /** 加密后密码 */
  private String encryptedPassword;
}
