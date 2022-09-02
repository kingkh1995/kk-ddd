package com.kk.ddd.support.model.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
public class UserAuthInfo implements Serializable {

  private Long id;

  private String name;

  /** 加密后密码 */
  private String encryptedPassword;
}
