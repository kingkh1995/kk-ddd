package com.kk.ddd.user.domain.strategy;

import com.kk.ddd.support.constant.AccountTypeEnum;
import org.springframework.stereotype.Component;

/**
 * username类型Account策略实现类 <br>
 *
 * @author KaiKoo
 */
@Component
public class UsernameAccountStrategy implements AccountStrategy {

  @Override
  public AccountTypeEnum getIdentifier() {
    return AccountTypeEnum.USERNAME;
  }
}
