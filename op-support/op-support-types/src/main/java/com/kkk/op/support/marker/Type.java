package com.kkk.op.support.marker;

import java.io.Serializable;

/**
 * DP 类 marker 接口 <br>
 * 等同于基本数据类型，具有不可变属性，变量应该全部设置为final且全为基本数据类型，String或枚举 <br>
 * 一、属性必须不能为空 二、使用valueOf从不靠谱的输入中构建DP对象 三、使用of方法从可靠的输入中构建DP对象
 *
 * @author KaiKoo
 */
public interface Type extends Serializable {}
