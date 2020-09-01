package io.tdi.jsf.core.utils.callback;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public interface Action0<V> {
  /**
   * 回调方法
   *
   * @return 任意返回值
   */
  V call();
}
