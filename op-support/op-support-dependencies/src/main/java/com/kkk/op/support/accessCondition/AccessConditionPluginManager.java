package com.kkk.op.support.accessCondition;

import com.kkk.op.support.annotation.MockResource;
import com.kkk.op.support.base.AbstractStrategyManager;
import java.util.EnumSet;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class AccessConditionPluginManager
    extends AbstractStrategyManager<AccessConditionPluginEnum, AccessConditionCheckPlugin> {

  public AccessConditionPluginManager() {
    super(EnumSet.of(CollectTactic.PRIMARY));
  }

  /**
   * 根据输入调用插件进行检查并判断
   *
   * @param obj 查询返回值
   * @param input 访问条件输入
   * @return 插件检查结果
   */
  @MockResource(mockMethod = "callPluginCheckMock") // 添加mock
  public boolean callPluginCheck(Object obj, String input) {
    var context = PluginCheckContext.buildFrom(input);
    log.info("Start checking with context '{}'......", context);
    var canAccess = super.getSingleton(AccessConditionPluginEnum.valueOf(context.plugin())).canAccess(obj, context.args());
    return context.checkResult(canAccess);
  }

  // 使用record 为不可变类 默认继承Record
  private record PluginCheckContext(boolean reverse, String plugin, String args) {

    static PluginCheckContext buildFrom(String input) {
      input = Objects.requireNonNull(input).strip();
      var reverse = false;
      if (input.startsWith("!")) {
        reverse = true;
        input = input.substring(1);
      }
      var index = input.indexOf(":");
      if (index > 0) {
        return new PluginCheckContext(reverse, input.substring(0, index), input.substring(index + 1));
      } else {
        return new PluginCheckContext(reverse, input, null);
      }
    }

    boolean checkResult(boolean canAccess) {
      return reverse() ? !canAccess : canAccess;
    }
  }

  // mock方法
  private boolean callPluginCheckMock(Object obj, String input) {
    return Boolean.parseBoolean(input.strip());
  }
}
