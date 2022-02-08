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

  @NotBlank private String encodedPassword;

  @Null private String plaintextPassword;
}
