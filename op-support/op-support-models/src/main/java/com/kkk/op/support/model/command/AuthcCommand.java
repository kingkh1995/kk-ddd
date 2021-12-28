package com.kkk.op.support.model.command;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import lombok.Data;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
public class AuthcCommand implements Serializable {

  @NotBlank private String username;

  @NotBlank private String encodedPassword;

  @Null private String plaintextPassword;
}
