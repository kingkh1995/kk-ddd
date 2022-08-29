package com.kk.ddd.support.model.command;

import com.kk.ddd.support.model.group.Inner;
import com.kk.ddd.support.model.group.Outer;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class AuthCommand implements Serializable {

  @NotBlank(message = "登录账号不能为空！")
  private String username;

  /** 编码后密码 */
  @NotBlank(message = "编码密码不能为空！", groups = Outer.class)
  private String encodedPassword;

  /** 明文密码 */
  @NotBlank(message = "明文密码不能为空！", groups = Inner.class)
  private transient String plaintextPassword;
}
