package org.kidal.jsf.core.utils.callback;

/**
 * 带返回值的回调
 *
 * @author kidal
 */
public interface Action2<V, A0, A1> {
  /**
   * 回调方法
   *
   * @param arg0 参数0
   * @param arg1 参数1
   * @return 任意返回值
   */
  V call(A0 arg0, A1 arg1);
}
