package com.kkk.op.support.accessCondition;

import java.util.LinkedList;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AccessConditionChecker检查器 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class AccessConditionChecker {

  private final AccessConditionPluginManager pluginManager;

  public AccessConditionChecker(@Autowired AccessConditionPluginManager pluginManager) {
    this.pluginManager = Objects.requireNonNull(pluginManager);
  }

  /**
   * 解析条件，支持短路 (A || B || (!C && D || (E || !F && G) && H)) || !I <br>
   * 例：要求操作人是创建人并且不能在小程序上操作 或 操作人拥有update权限且在内网操作 <br>
   * (creator && !source:MP || permission:update && source:LAN)
   *
   * @param condition 条件表达式，只支持短路符号，括号和非
   * @return 解析后校验的结果
   */
  public boolean analyzeThenCheck(Object obj, @NotBlank String condition) {
    log.info("start analyzing with condition: {}", condition);
    if (condition == null || condition.isBlank()) {
      throw new AccessConditionAnalyzeException("analyzing error! condition is blank!");
    }
    // 维护一个窗口
    var l = 0;
    var r = 0;
    // 开始解析
    try {
      var aStack = new LinkedList<Character>();
      var bStack = new LinkedList<Boolean>();
      var chars = condition.toCharArray();
      while (r < chars.length) {
        var c = chars[r];
        if (c == '(' || c == ')' || c == '&' || c == '|') {
          // 对窗口内定义的条件进行检查
          executeCheck(obj, condition.substring(l, r), bStack);
          // 处理
          if (c == '(') {
            r++;
            aStack.push(c);
          } else if (c == ')') {
            r++;
            // 先pop一次则为括号内的结果，前面的表达式都未发生短路，所以直接以最后一个结果来判断
            var canAcess = bStack.pop();
            // 继续pop直到上一个 ( ，必然会遇到因为要求是规范的表达式
            while (aStack.pop() != '(') {
              bStack.pop();
            }
            // 结果入栈
            bStack.push(canAcess);
          } else {
            // 因为默认只支持 && 和 || 故右端移动两位
            r += 2;
            // & 和 | 情况下，判断是否需要短路
            var canAcess = bStack.pop();
            if ((c == '&' && !canAcess) || (c == '|' && canAcess)) {
              // 短路，向前和前后去除连续的相同逻辑符号的表达式
              // 向前处理栈
              while (!aStack.isEmpty() && aStack.peek() == c) {
                aStack.pop();
                bStack.pop();
              }
              // 向后移动窗口右端，并忽略掉后面同级的()
              for (var count = 0; r < chars.length; r++) {
                var cursor = chars[r];
                if (cursor == '(') {
                  count++;
                } else if (count == 0 && (cursor == ')' || cursor + c == 162)) { // &-38 |-124
                  break;
                } else if (cursor == ')') {
                  count--;
                }
              }
            } else {
              // 非短路则符号也入栈
              aStack.push(c);
            }
            // 结果入栈
            bStack.push(canAcess);
          }
          // 最后移动窗口左端
          l = r;
        } else {
          r++;
        }
      }
      // 最后如果窗口长度不为0，则还需要检查一次
      if (r != l) {
        executeCheck(obj, condition.substring(l, r), bStack);
      }
      // 始终返回栈顶
      return bStack.pop();
    } catch (Exception e) {
      log.error("analyzing error!, l:{}, r:{}", l, r, e);
      throw new AccessConditionAnalyzeException("access condition analyzing error!", e);
    }
  }

  private void executeCheck(Object obj, String input, LinkedList<Boolean> stack) {
    if (input.isBlank()) {
      return;
    }
    var canAccess = this.pluginManager.callPluginCheck(obj, input);
    stack.push(canAccess);
    log.info("check finish, input:{}, canAccess:{}", input, canAccess);
  }

  private class AccessConditionAnalyzeException extends RuntimeException {

    public AccessConditionAnalyzeException(String message) {
      super(message);
    }

    public AccessConditionAnalyzeException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
