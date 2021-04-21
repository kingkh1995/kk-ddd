package com.kkk.op.support.models.command;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author KaiKoo
 */
@Data
public class AccountUpdateCommand implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private Long userId;

}