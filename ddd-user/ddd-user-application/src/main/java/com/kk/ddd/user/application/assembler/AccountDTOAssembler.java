package com.kk.ddd.user.application.assembler;

import com.kk.ddd.support.model.dto.AccountDTO;
import com.kk.ddd.support.type.CommonTypesMapper;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.mapper.UserTypesMapper;
import com.kk.ddd.user.domain.type.UserId;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 使用Mapstruct生成转换类，实现方式是编译期间直接生成该接口实现类的字节码文件 <br>
 *
 * @author KaiKoo
 */
@Mapper(
    componentModel = "spring", // 使用spring依赖注入
    imports = {}, // 可注入外部类，以在@Mapping注解表达式中使用，否则需要指定外部类的全路径
    uses = {CommonTypesMapper.class, UserTypesMapper.class}, // 注入外部映射类
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, // 配置总是对参数进行空检查
    nullValueMappingStrategy =
        NullValueMappingStrategy.RETURN_NULL, // 源为空则返回null，配置RETURN_DEFAULT则返回空对象
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE // 如果属性为空，则忽略
    )
public interface AccountDTOAssembler {

  // 该方法是通过类加载器按类名（接口类名加上Impl后缀）找到编译后的实现类，并创建一个实例对象。
  // AccountDTOAssembler INSTANCE = Mappers.getMapper(AccountDTOAssembler.class);

  /**
   * Mapping注解使用 <br>
   * - target：目标属性，将源的bean类型属性自动拆包映射到普通属性时使用'.'表示，不需要逐条定义； <br>
   * - dateFormat：日期类型映射格式； - numberFormat：数字类型映射格式，使用DecimalFormat实现； <br>
   * - constant：使用固定值； <br>
   * - expression：使用表达式进行映射，java(...)，如使用了外部类，需要指定路径，或通过@Mapper注解imports属性注入； <br>
   * - defaultExpression：如果源为空，则执行该表达式； <br>
   * - ignore：忽略映射该属性 <br>
   * - qualifiedBy、qualifiedByName：使用指定的映射方法 <br>
   * - resultType：指定返回值的类型，适用于返回值为父类的情况 <br>
   * - defaultValue：如果源为空，则使用默认值 <br>
   * - nullValuePropertyMappingStrategy：源为空时处理方法，默认SET_TO_NULL，即设置为null，IGNORE表示不处理 <br>
   * - NullValueCheckStrategy：参数空检查策略，最好设置为ALWAYS
   */
  @Mapping(target = "createTimestamp", source = "createTime")
  @Mapping(target = "updateTimestamp", source = "updateTime")
  AccountDTO toDTO(Account account);

  @InheritInverseConfiguration(name = "toDTO") // 注解在反向映射方法上，会自动继承大部分配置
  @Mapping(target = "id", qualifiedByName = "map2AccountId-new") // 指定特定的类型映射方法
  Account fromDTO(AccountDTO accountDTO);

  // 集合会自动调用上面的单对象转换方法，同时设置源为空时，返回空集合
  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<AccountDTO> toDTO(List<Account> accounts);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<Account> fromDTO(List<AccountDTO> accountDTOS);

  // 更新对象则使用@MappingTarget注解标识目标对象参数
  // 可以使用多个源映射，如果存在相同属性则需要手动指定
  @Mapping(target = "createTimestamp", source = "account.createTime")
  @Mapping(target = "userId", source = "userId")
  void buildDTO(UserId userId, Account account, @MappingTarget AccountDTO accountDTO);

  /** 自定义工厂方法 */
  default AccountDTO createAccountDTO() {
    return new AccountDTO();
  }
}
