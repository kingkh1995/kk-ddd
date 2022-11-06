package com.kk.ddd.user.domain.mapper;

import com.kk.ddd.support.constant.AccountTypeEnum;
import com.kk.ddd.support.constant.UserStateEnum;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.AccountType;
import com.kk.ddd.user.domain.type.AuthStrength;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.domain.type.UserState;
import com.kk.ddd.user.domain.type.Username;
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
  public Long mapFromUserId(final UserId userId) {
    return userId.getValue();
  }

  public UserId map2UserId(final Long l) {
    return UserId.valueOf(l, "账户ID");
  }

  public String mapFromUsername(final Username username) {
    return username.getValue();
  }

  public Username map2Username(final String s) {
    return Username.valueOf(s, "用户名");
  }

  // 枚举可以映射为字符串，会自动调用name()方法。
  public UserStateEnum mapFromUserState(final UserState userState) {
    return userState.toEnum();
  }

  public UserState map2UserState(final String s) {
    return UserState.valueOf(s, "用户状态");
  }

  public Long mapFromAccountId(final AccountId accountId) {
    return accountId.getValue();
  }

  public AccountId map2AccountId(final Long l) {
    return AccountId.valueOf(l, "账户ID");
  }

  @Named("map2AccountId-new") // 存在多个相同的类型映射方法时进行标识
  public AccountId map2AccountIdNew(final Long l) {
    log.info("this is map2AccountId-new!");
    return AccountId.of(l);
  }

  public AccountTypeEnum mapFromAccountType(final AccountType accountType) {
    return accountType.toEnum();
  }

  public AccountType map2AccountType(final String s) {
    return AccountType.valueOf(s, "账户类型");
  }

  public Integer mapFromAuthStrength(final AuthStrength authStrength) {
    return authStrength.getValue();
  }

  public AuthStrength map2AuthStrength(Integer i) {
    return AuthStrength.valueOf(i, "验证强度");
  }
}
