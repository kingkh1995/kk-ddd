package com.kk.ddd.support.access;

import java.util.LinkedList;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AccessConditionChecker检查器 todo... 参考@pointcut
 *
 * @author KaiKoo
 */
@Slf4j
@RequiredArgsConstructor
public class AccessConditionChecker {

  private final AccessConditionPluginManager pluginManager;

  /**
   * 解析条件，支持短路 (A || B || (!C && D || (E || !F && G) && H)) || !I <br>
   * 例：要求操作人是创建人并且不能在小程序上操作 或 操作人拥有update权限且在内网操作 <br>
   * (creator && !source:MP || permit:update && source:LAN)
   *
   * @param condition 条件表达式，只支持短路符号，括号和非
   * @return 解析后校验的结果
   */
  public boolean analyzeThenCheck(Object obj, @NotBlank String condition) {
    log.info("Start analyzing with condition '{}'......", condition);
    if (condition == null || condition.isBlank()) {
      throw new AccessConditionCheckException("Analyzing error for condition is blank!");
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
          executeCheck(obj, condition.substring(l, r++), bStack);
          // 分析处理
          if (c == '(') {
            aStack.push(c);
          } else if (c == ')') {
            // 先pop一次则为括号内的结果，前面的表达式都未发生短路，所以直接以最后一个结果来判断
            var canAccess = bStack.pop();
            // 继续pop直到上一个 ( ，必然会遇到因为要求是规范的表达式
            while (aStack.pop() != '(') {
              bStack.pop();
            }
            // 结果入栈
            bStack.push(canAccess);
          } else {
            // 因为默认只支持 && 和 || 故右端需要移动两位
            r++;
            // & 和 | 情况下，判断是否需要短路
            var canAccess = bStack.pop();
            if ((c == '&' && !canAccess) || (c == '|' && canAccess)) {
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
            bStack.push(canAccess);
          }
          // 最后重置窗口
          l = r;
        } else {
          r++;
        }
      }
      // 分析完成后如果窗口长度不为0，则还需要检查一次
      if (r != l) {
        executeCheck(obj, condition.substring(l, r), bStack);
      }
      // 始终返回栈顶
      var checkPass = bStack.pop();
      log.info("Analyze finish! result = {}.", checkPass);
      return checkPass;
    } catch (Exception e) {
      log.error("Analyzing error! l = {}, r = {}.", l, r, e);
      throw new AccessConditionCheckException("Analyzing access condition error!");
    }
  }

  private void executeCheck(Object obj, String input, LinkedList<Boolean> stack) {
    if (input.isBlank()) {
      return;
    }
    var canAccess = this.pluginManager.callPluginCheck(obj, input);
    stack.push(canAccess);
    log.info("Check finish, input '{}', canAccess '{}'.", input, canAccess);
  }
}
