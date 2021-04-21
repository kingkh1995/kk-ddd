package com.kkk.op.support.models.command;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author KaiKoo
 */
@Data
public class AccountCreateCommand implements Serializable {

    @NotNull
    private Long userId;

}
