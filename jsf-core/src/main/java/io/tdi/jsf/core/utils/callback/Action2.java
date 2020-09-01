package io.tdi.jsf.core.utils.callback;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
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
