package com.kkk.op.user.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.types.LongId;
import com.kkk.op.support.types.PageSize;
import com.kkk.op.support.types.StampedTime;
import com.kkk.op.support.types.TenThousandYuan;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.AccountState;
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

  @Test
  void test() {}

  @Test
  void testJacksonWithType() {
    System.out.println(kson.writeJson(PageSize.DEFAULT_SIZE));
    LongId longId = LongId.from(123456789L);
    System.out.println(kson.writeJson(longId));
    AccountId accountId = AccountId.from(123456789L);
    System.out.println(kson.writeJson(accountId));
    var id1 = kson.readJson("123E4", new TypeReference<LongId>() {});
    var id2 = kson.readJson("111e9", new TypeReference<AccountId>() {});
    System.out.println(id1.value());
    System.out.println(id2.longValue());
    System.out.println(
        kson.readJson(kson.writeJson("66666"), new TypeReference<AccountId>() {}).toPlainString());
    var accountState = AccountState.of(AccountStateEnum.INIT);
    var json = kson.writeJson(accountState);
    System.out.println(kson.readJson(json, new TypeReference<AccountState>() {}).getValue());
    var tJson = kson.writeJson(StampedTime.current());
    System.out.println(tJson);
    System.out.println(kson.readJson(tJson, StampedTime.class).toLocalDateTime());
    var account =
        Account.builder()
            .id(AccountId.from(10))
            .state(AccountState.of(AccountStateEnum.ACTIVE))
            .createTime(LocalDateTime.now())
            .build();
    var s = kson.writeJson(account);
    System.out.println(s);
    System.out.println(kson.readJson(s, Account.class));
    System.out.println(TenThousandYuan.from(new BigDecimal("110.6")).toPlainString());
  }
}
