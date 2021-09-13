package com.kkk.op.support.accessCondition;

import com.kkk.op.support.annotation.MockResource;
import com.kkk.op.support.base.AbstractStrategyManager;
import java.util.EnumSet;
import lombok.ToString;
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
    var context = new PluginCheckContext(input);
    log.info("start checking with context:{}", context);
    // 不做异常处理了，因为不允许为空
    var pluginEnum = AccessConditionPluginEnum.valueOf(context.plugin);
    var result = this.getSingleton(pluginEnum).canAcess(obj, context.args);
    return context.reverse ? !result : result;
  }

  // mock方法
  public boolean callPluginCheckMock(Object obj, String input) {
    return Boolean.parseBoolean(input.strip());
  }

  @ToString
  private class PluginCheckContext {
    boolean reverse = false;
    String plugin;
    String args;

    private PluginCheckContext(String input) {
      input = input.strip();
      if (input.startsWith("!")) {
        this.reverse = true;
        input = input.substring(1);
      }
      var index = input.indexOf(":");
      if (index > 0) {
        this.plugin = input.substring(0, index);
        this.args = input.substring(index + 1);
      } else {
        this.plugin = input;
      }
    }
  }
}
