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
        Account account = Account.builder().id(new LongId(id)).build().find(accountService);
        return accountDTOAssembler.toDTO(account);
    }

    @Override
    public void remove(AccountDTO dto) {
        Account account = accountDTOAssembler.fromDTO(dto);
        account.remove(accountService);
    }

    @Override
    public void save(AccountDTO dto) {
        Account account = accountDTOAssembler.fromDTO(dto);
        account.save(accountService);
    }
}
