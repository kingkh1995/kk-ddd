package com.kkk.op.user.application.service.impl;

import com.kkk.op.support.models.user.AccountDTO;
import com.kkk.op.support.models.user.AccountQueryDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.application.service.AccountApplicationService;
import com.kkk.op.user.assembler.AccountDTOAssembler;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.query.entity.AccountQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KaiKoo
 */
@Service
public class AccountApplicationServiceImpl implements AccountApplicationService {

    private final AccountDTOAssembler accountDTOAssembler = AccountDTOAssembler.INSTANCE;

    private final AccountService accountService;

    public AccountApplicationServiceImpl(@Autowired AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public AccountDTO find(Long id) {
        var query = AccountQuery.builder().id(new LongId(id)).build();
        return accountDTOAssembler.toDTO(query.find(accountService));
    }

    @Override
    public void remove(Long id) {
        var account = Account.builder().id(new LongId(id)).build();
        account.remove(accountService);
    }

    @Override
    public Long save(AccountDTO dto) {
        // 转换对象
        var account = accountDTOAssembler.fromDTO(dto);
        // 行为发生
        account.save(accountService);
        // todo... 触发事件

        // 返回id
        return account.getId().getId();
    }

    @Override
    public List<AccountDTO> list(AccountQueryDTO queryDTO) {
        var accountQuery = accountDTOAssembler.toQuery(queryDTO);
        var list = accountQuery.list(accountService);
        // todo... 实现
        return null;
    }

}
