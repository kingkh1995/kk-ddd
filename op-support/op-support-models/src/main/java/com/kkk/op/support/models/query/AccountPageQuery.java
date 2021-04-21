package com.kkk.op.support.models.query;

import lombok.Data;

/**
 *
 * @author KaiKoo
 */
@Data
public class AccountPageQuery extends AbstractPageQuery {

    private Long id;

    private Long userId;

    private String status;

    private Long[] ids;

}
