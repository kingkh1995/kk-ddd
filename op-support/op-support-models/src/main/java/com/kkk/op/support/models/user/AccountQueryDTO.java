package com.kkk.op.support.models.user;

import com.kkk.op.support.models.base.AbstractQueryDTO;
import lombok.Data;

/**
 *
 * @author KaiKoo
 */
@Data
public class AccountQueryDTO extends AbstractQueryDTO {

    private Long id;

    private Long userId;

    private String status;

}
