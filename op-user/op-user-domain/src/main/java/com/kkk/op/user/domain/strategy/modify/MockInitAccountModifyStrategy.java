package com.kkk.op.user.domain.strategy.modify;

import com.kkk.op.support.enums.AccountStateEnum;
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
@Primary
@Order(Ordered.HIGHEST_PRECEDENCE)
@Qualifier("mock")
@Component // 加上注解才能被ApplicationContext获取到
public class MockInitAccountModifyStrategy implements AccountModifyStrategy {

  @Override
  public AccountStateEnum getStrategyID() {
    return AccountStateEnum.INIT;
  }
}
