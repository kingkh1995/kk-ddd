package com.kkk.op.support.model.command;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class AuthcCommand implements Serializable {

  @NotBlank private String username;

  /** 编码后密码 */
  @NotBlank private String encodedPassword;

  /** 明文密码 */
  @Null private String plaintextPassword;
}
