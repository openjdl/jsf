package org.kidal.jsf.core.utils.callback;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
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
