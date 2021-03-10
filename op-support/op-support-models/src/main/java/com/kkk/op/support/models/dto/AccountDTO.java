package com.kkk.op.support.models.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author KaiKoo
 */
@Data
public class AccountDTO implements Serializable {

    private Long id;

    @NotNull
    private Long userId;

    private String status;
}
