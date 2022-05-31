package com.kk.ddd.user.domain.mapper;

import com.kk.ddd.support.enums.AccountStateEnum;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.AccountState;
import com.kk.ddd.user.domain.type.UserId;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * Mapstruct 类型映射公共类 <br>
 * 方法需要声明为公共普通方法，加载到Mapper中后会被自动调用 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
public class UserTypesMapper {

  /** 自定义类型转换方法 */
  public Long mapFromUserId(UserId userId) {
    return userId.getValue();
  }

  public UserId map2UserId(Long l) {
    return UserId.valueOf(l, "用户ID");
  }

  public Long mapFromAccountId(AccountId accountId) {
    return accountId.getValue();
  }

  public AccountId map2AccountId(Long l) {
    return AccountId.valueOf(l, "账户ID");
  }

  @Named("map2AccountId-new") // 存在多个相同的类型映射方法时进行标识
  public AccountId map2AccountIdNew(Long l) {
    log.info("this is map2AccountId-new!");
    return AccountId.of(l);
  }

  // 可以直接映射为枚举，会自动调用name()方法转为字符串
  public AccountStateEnum mapFromAccountState(AccountState accountState) {
    return accountState.getValue();
  }

  public AccountState map2AccountState(String s) {
    return AccountState.valueOf(s, "账户状态");
  }
}
