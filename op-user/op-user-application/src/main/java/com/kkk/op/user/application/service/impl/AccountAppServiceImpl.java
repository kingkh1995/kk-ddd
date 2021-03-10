package com.kkk.op.user.application.service.impl;

import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.type.LongId;
import com.kkk.op.user.application.service.AccountAppService;
import com.kkk.op.user.assembler.AccountDTOAssembler;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KaiKoo
 */
@Service
public class AccountAppServiceImpl implements AccountAppService {

    private final AccountService accountService;
    private final AccountDTOAssembler accountDTOAssembler;

    public AccountAppServiceImpl(@Autowired AccountService accountService) {
        this.accountService = accountService;
        this.accountDTOAssembler = AccountDTOAssembler.getInstance();
    }

    @Override
    public AccountDTO find(Long id) {
        return accountDTOAssembler.toDTO(accountService.find(new LongId(id)));
    }

    @Override
    public void remove(Long id) {
        var account = Account.builder().id(new LongId(id)).build();
        account.remove(accountService);
    }

    @Override
    public Long save(AccountDTO dto) {
        // 转换对象
        Account account = accountDTOAssembler.fromDTO(dto);
        // 行为发生
        account.save(accountService);
        //todo... 触发事件

        // 返回id
        return account.getId().getValue();
    }
}
