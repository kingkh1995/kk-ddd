package com.kkk.op.support.marker;

import java.io.Serializable;

/**
 * DP 类 marker 接口 <br>
 * 等同于基本数据类型，具有不可变属性，变量应该全部设置为final且全为基本数据类型，String或枚举 <br>
 * 一、属性必须为final且不能为空 <br>
 * 二、使用valueOf从不靠谱的输入中构建DP对象；使用from方法从可靠的输入中构建DP对象（假定参数都是合法的） <br>
 * 三、构造方法为私有，且上面公共静态方法都基于私有静态of方法实现 <br>
 * 附、通过jackson的注解 @JsonCreator（于from方法） @JsonProperty @JsonValue（于属性） 实现自定义序列化和反序列化
 *
 * @author KaiKoo
 */
public interface Type extends Serializable {}
