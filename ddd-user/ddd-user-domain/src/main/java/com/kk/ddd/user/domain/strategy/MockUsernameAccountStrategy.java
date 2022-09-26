package com.kk.ddd.user.domain.strategy;

import com.kk.ddd.support.enums.AccountTypeEnum;
import com.kk.ddd.user.domain.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 策略实现类Mock类 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Primary
@Order(Ordered.HIGHEST_PRECEDENCE)
@Qualifier("mock")
@Component // 加上注解才能被ApplicationContext获取到
public class MockUsernameAccountStrategy implements AccountStrategy {

  @Override
  public AccountTypeEnum getIdentifier() {
    return AccountTypeEnum.USERNAME;
  }

  @Override
  public boolean allowModify(Account oldAccount, Account newAccount) {
    log.info("This is mock, return ture!");
    return true;
  }
}
