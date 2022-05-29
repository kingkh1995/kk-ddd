package com.kkk.op.support.util.strategy;

/**
 * 策略类marker <br>
 * * 策略类不应该直接操作对象，而是通过返回计算后的值，在 Domain Service 里对对象进行操作 <br>
 *
 * @author KaiKoo
 */
public interface Strategy<K> {

  K getIdentifier();
}
