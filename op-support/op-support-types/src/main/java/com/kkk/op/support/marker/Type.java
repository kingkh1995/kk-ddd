package com.kkk.op.support.marker;

import java.io.Serializable;

/**
 * DP 类 marker 接口 <br>
 * *设计参考 Guava UnsignedInteger* <br>
 * 等同于基本数据类型，具有不可变属性，变量应该全部设置为final且全为基本数据类型，String或枚举 <br>
 * 一、属性必须为final且类型为基本数据类型； <br>
 * 二、使用valueOf（Object参数）从不可靠的输入中构建DP对象；<br>
 * 三、使用from（相似或同类型其他对象）和of（自身属性类型参数）方法从可靠的输入中构建DP对象，不需要参数校验； <br>
 * 四、只能有一个私有的构造器，如果存在特定逻辑（如存在缓存），需要提取出私有静态方法供其他公共静态方法调用。 <br>
 * 附、通过jackson的注解 @JsonCreator（于from方法） @JsonProperty @JsonValue（于属性） 实现自定义序列化和反序列化
 *
 * @author KaiKoo
 */
public interface Type extends Serializable {}
