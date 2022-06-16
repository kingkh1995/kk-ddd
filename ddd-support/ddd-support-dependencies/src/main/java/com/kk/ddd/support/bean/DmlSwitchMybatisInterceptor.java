package com.kk.ddd.support.bean;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * DML控制Mybatis拦截器 <br>
 * type：拦截的对象 <br>
 * Executor拦截执行器的方法；<br>
 * ParameterHandler拦截参数的处理；<br>
 * ResultHandler拦截结果集的处理；<br>
 * StatementHandler拦截Sql语法构建的处理。 <br>
 * method：拦截对象的方法 <br>
 * args：被拦截方法的参数列表 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@RefreshScope
@Intercepts(
    @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}))
public class DmlSwitchMybatisInterceptor implements Interceptor {

  @Value("${sql.dml-disable:false}")
  private boolean dmlDisable;

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    if (dmlDisable) {
      // BoundSql只负责保存sql和parameter，只获取预编译之后的sql的话parameter传null即可。
      log.debug(
          "Preparing: {}", ((MappedStatement) invocation.getArgs()[0]).getBoundSql(null).getSql());
      throw new PersistenceException("System is updating, please try later!");
    }
    return invocation.proceed();
  }

  // 拦截判断，一般不需要重写，默认判断拦截器的签名和被拦截对象的接口是否匹配，如果匹配则为拦截对象创建jdk动态代理对象。
  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }
}
