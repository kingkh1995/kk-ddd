package com.kk.ddd.support.access;

import com.kk.ddd.support.util.ApplicationContextAwareSingleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 基于spel表达式的校验器 <br>
 * example: #this.checkArgs(#_args) && @mockCheckPlugin.canAccess(#_result, 'test') <br>
 * #this表示QueryService对象；使用@获取Bean。
 *
 * @author kingk
 */
@Slf4j
@RequiredArgsConstructor
public class SpelAccessConditionChecker extends ApplicationContextAwareSingleton
    implements AccessConditionChecker {

  public static final String ARGS = "_args";
  public static final String RESULT = "_result";

  @Override
  public boolean checkAccessConditionBefore(String accessCondition, Object target, Object[] args) {
    var context = new StandardEvaluationContext(target);
    context.setBeanResolver(new BeanFactoryResolver(getApplicationContext()));
    context.setVariable(ARGS, args);
    return Boolean.TRUE.equals(
        new SpelExpressionParser().parseRaw(accessCondition).getValue(context, Boolean.class));
  }

  @Override
  public boolean checkAccessConditionAfter(String accessCondition, Object target, Object Result) {
    var context = new StandardEvaluationContext(target);
    context.setBeanResolver(new BeanFactoryResolver(getApplicationContext()));
    context.setVariable(RESULT, Result);
    return Boolean.TRUE.equals(
        new SpelExpressionParser().parseRaw(accessCondition).getValue(context, Boolean.class));
  }

  @Override
  public void afterSingletonsInstantiated() {
    // do nothing
  }
}
