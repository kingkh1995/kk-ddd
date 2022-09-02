package com.kk.ddd.support.core;

import java.io.Serializable;

/**
 * DP 类 marker 接口 <br>
 * DP设计参考 Guava UnsignedInteger，等同于基本数据类型，为不可变对象。 <br>
 * 一、属性必须为final且类型为基本数据类型、String或枚举； <br>
 * 二、只能有一个私有的构造器，且不包含任何逻辑，如果存在特定逻辑（如存在缓存），需要提取出私有静态方法供其他公共静态方法调用。 <br>
 * 三、使用私有的of静态方法封装特定逻辑（校验和缓存等），供其他公共静态方法调用；<br>
 * 四、使用of（自身属性类型参数）和from（相似或同类型其他对象）方法从可靠的输入中构建DP对象，不需要参数校验； <br>
 * 五、使用valueOf（Object参数）从不可靠的输入中构建DP对象，需要参数校验 ；<br>
 * 附、jackson的注解：反序列化使用 @JsonCreator（于of方法），序列化使用 @JsonProperty 或 @JsonValue（于属性或方法）。
 *
 * @author KaiKoo
 */
public interface Type extends Serializable {}
