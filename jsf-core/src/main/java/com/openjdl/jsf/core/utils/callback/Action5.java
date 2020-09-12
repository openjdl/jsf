package com.openjdl.jsf.core.utils.callback;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public interface Action5<V, A0, A1, A2, A3, A4> {
  /**
   * 回调方法
   *
   * @param arg0 参数0
   * @param arg1 参数1
   * @param arg2 参数2
   * @param arg3 参数3
   * @param arg4 参数4
   * @return 任意返回值
   */
  V call(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
}
