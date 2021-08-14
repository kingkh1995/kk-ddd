package com.kkk.op.support.accessCondition;

import com.kkk.op.support.base.AbstractStrategyManager;
import java.util.EnumSet;
import java.util.Objects;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class AccessConditionCheckManager
    extends AbstractStrategyManager<AccessConditionPluginEnum, AccessConditionCheckPlugin> {

  public AccessConditionCheckManager() {
    super(EnumSet.of(CollectTactic.PRIMARY));
  }

  public boolean pluginCheck(String input) {
    var context = new PluginCheckContext(input);
    log.info("start checking with context:{}", context);
    // 不做异常处理了，因为不允许为空
    var pluginEnum = AccessConditionPluginEnum.valueOf(context.plugin);
    var result = Objects.requireNonNull(this.getSingleton(pluginEnum)).canAcess(context.args);
    return context.reverse ? !result : result;
  }

  @ToString
  private static class PluginCheckContext {
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
