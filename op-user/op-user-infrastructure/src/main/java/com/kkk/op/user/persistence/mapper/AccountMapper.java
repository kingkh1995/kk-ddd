package com.kkk.op.user.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kkk.op.user.persistence.model.AccountDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper 接口 <br>
 * （方法名使用：insert delete update select selectList selectPage） <br>
 * todo... 分页实现
 *
 * @author KaiKoo
 */
@Mapper // 使用Mybatis-Plus后Mapper注解可以不加
public interface AccountMapper extends BaseMapper<AccountDO> {}
