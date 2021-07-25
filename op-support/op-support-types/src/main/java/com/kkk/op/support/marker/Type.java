package com.kkk.op.support.marker;

import java.io.Serializable;

/**
 * DP 类 marker 接口 <br>
 * 等同于基本数据类型，具有不可变属性，变量应该全部设置为final且全为基本数据类型，String或枚举 <br>
 * 一、属性必须不能为空 <br>
 * 二、可通用的DP类，使用valueOf从不靠谱的输入中构建DP对象（通过调用of方法实现）；使用of方法从可靠的输入中构建DP对象（假定参数都是合法的） <br>
 * 三、非通用的DP类或多个属性的DP类，使用from方法构建对象
 *
 * @author KaiKoo
 */
public interface Type extends Serializable {}
