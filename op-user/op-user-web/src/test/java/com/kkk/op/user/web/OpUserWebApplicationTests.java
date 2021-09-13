package com.kkk.op.user.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.type.LongId;
import com.kkk.op.support.type.PageSize;
import com.kkk.op.support.type.StampedTime;
import com.kkk.op.support.type.TenThousandYuan;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.AccountState;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.persistence.mapper.UserMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class OpUserWebApplicationTests {

  @Autowired private Kson kson;

  @Autowired private UserMapper userMapper;

  @Autowired private AccountMapper accountMapper;

  @Test
  void testMybatis() {
    var userDOList = userMapper.selectList();
    System.out.println(kson.writeJson(userDOList));
    var userDO = userMapper.selectById(userDOList.get(0).getId()).get();
    userDO.setGender(null);
    userDO.setAge(null);
    userDO.setEmail(null);
    userMapper.updateById(userDO);
    System.out.println(kson.writeJson(userMapper.selectById(userDO.getId())));
    var accountDOS = accountMapper.selectListByUserId(userDO.getId());
    var accountDO = accountDOS.get(0);
    accountDO.setState(null);
    accountMapper.updateById(accountDO);
    System.out.println(kson.writeJson(accountMapper.selectById(accountDO.getId())));
    var page =
        PageHelper.startPage(2, 1)
            .doSelectPage(() -> userMapper.selectListByGender(userDO.getGender()));
    System.out.println(kson.writeJson(page));
    System.out.println(accountMapper.deleteByUserId(userDOList.get(1).getId()));
  }

  @Test
  void testJacksonWithType() {
    System.out.println(kson.writeJson(PageSize.DEFAULT_SIZE));
    LongId longId = LongId.from(123456789L);
    System.out.println(kson.writeJson(longId));
    AccountId accountId = AccountId.from(123456789L);
    System.out.println(kson.writeJson(accountId));
    var id1 = kson.readJson("1234", new TypeReference<LongId>() {});
    var id2 = kson.readJson("111", new TypeReference<AccountId>() {});
    System.out.println(id1.value());
    System.out.println(id2.getValue());
    System.out.println(
        kson.readJson(kson.writeJson("66666"), new TypeReference<AccountId>() {}).toPlainString());
    var accountState = AccountState.from(AccountStateEnum.INIT);
    var json = kson.writeJson(accountState);
    System.out.println(kson.readJson(json, new TypeReference<AccountState>() {}).getValue());
    var tJson = kson.writeJson(StampedTime.current());
    System.out.println(tJson);
    System.out.println(kson.readJson(tJson, StampedTime.class).toLocalDateTime());
    var account =
        Account.builder()
            .id(AccountId.from(10))
            .state(AccountState.from(AccountStateEnum.ACTIVE))
            .createTime(LocalDateTime.now())
            .build();
    var s = kson.writeJson(account);
    System.out.println(s);
    System.out.println(kson.readJson(s, Account.class));
    System.out.println(TenThousandYuan.from(new BigDecimal("110.6")).toPlainString());
  }
}
