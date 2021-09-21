package com.kkk.op.support.changeTracking;

/**
 * 快照拍摄者 <br>
 *
 * @author KaiKoo
 */
@FunctionalInterface
public interface Snapshooter<T> {

  T snapshoot(T t);

  // 参考Function.identity() 直接返回原值
  static <T> Snapshooter<T> identity() {
    return t -> t;
  }
}
