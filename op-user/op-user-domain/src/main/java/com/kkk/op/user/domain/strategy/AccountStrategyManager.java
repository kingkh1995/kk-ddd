package com.kkk.op.user.domain.strategy;

import com.kkk.op.support.base.AbstractStrategyManager;
import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.strategy.modify.AccountModifyStrategy;
import java.util.EnumMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 策略类不应该直接操作对象，而是通过返回计算后的值，在 Domain Service 里对对象进行操作 <br>
 * todo... 待优化
 *
 * @author KaiKoo
 */
@Component // 配合InitializingBean使用
public class AccountStrategyManager extends AbstractStrategyManager {

  // Modify Strategy  使用EnumMap
  public EnumMap<AccountStatusEnum, AccountModifyStrategy> modifyStrategyMap;

  @Override
  public void afterPropertiesSet() {
    if (this.modifyStrategyMap != null) {
      return;
    }
    // 构造map
    this.modifyStrategyMap =
        this.applicationContext.getBeansOfType(AccountModifyStrategy.class).values().stream()
            .collect(
                Collectors.toMap(
                    AccountModifyStrategy::getStatusEnum,
                    Function.identity(),
                    (s, s2) -> s.getClass().getAnnotation(Primary.class) == null ? s2 : s,
                    () -> new EnumMap<>(AccountStatusEnum.class)));
  }

  public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    var accountModifyStrategy = this.modifyStrategyMap.get(oldAccount.getStatus().getValue());
    if (accountModifyStrategy != null) {
      return accountModifyStrategy.allowModify(oldAccount, newAccount);
    }
    return false;
  }
}
