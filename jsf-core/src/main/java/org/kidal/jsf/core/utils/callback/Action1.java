package org.kidal.jsf.core.utils.callback;

/**
 * 带返回值的回调
 *
 * @author kidal
 */
public interface Action1<V, A0> {
  /**
   * 回调方法
   *
   * @param arg0 参数0
   * @return 任意返回值
   */
  V call(A0 arg0);
}
