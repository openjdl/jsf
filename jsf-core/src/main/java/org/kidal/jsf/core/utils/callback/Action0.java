package org.kidal.jsf.core.utils.callback;

/**
 * 带返回值的回调
 *
 * @author kidal
 */
public interface Action0<V> {
  /**
   * 回调方法
   *
   * @return 任意返回值
   */
  V call();
}
