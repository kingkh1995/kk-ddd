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

  private String encryptedPassword;
}
