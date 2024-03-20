package com.kk.ddd.support.access;

import com.kk.ddd.support.annotation.MockResource;
import com.kk.ddd.support.util.strategy.AbstractStrategyManager;
import java.util.EnumSet;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * AccessConditionPluginManager <br>
 * 键不使用枚举是为了能够被拓展，因为枚举无法被继承。
 *
 * @author KaiKoo
 */
@Slf4j
public class SimpleAccessConditionPluginManager
    extends AbstractStrategyManager<String, AccessConditionCheckPlugin> {

  public SimpleAccessConditionPluginManager() {
    super(EnumSet.of(CollectTactic.PRIMARY));
  }

  @Override
  protected Class<AccessConditionCheckPlugin> getSClass() {
    return AccessConditionCheckPlugin.class;
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
    var canAccess = super.getSingleton(context.plugin()).canAccess(obj, context.args());
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
        return new PluginCheckContext(
            reverse, input.substring(0, index), input.substring(index + 1));
      } else {
        return new PluginCheckContext(reverse, input, null);
      }
    }

    boolean checkResult(boolean canAccess) {
      return reverse() != canAccess;
    }
  }

  // mock方法
  private boolean callPluginCheckMock(Object obj, String input) {
    return Boolean.parseBoolean(input.strip());
  }
}
