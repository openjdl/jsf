package org.kidal.jsf.core;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-04 23:15:34
 *
 * @author kidal
 * @since 0.1.0
 */
public interface JsfService {
  /**
   * 获取服务ID
   *
   * @return 服务ID.
   */
  @NotNull
  String getJsfServiceName();

  /**
   * 初始化服务.
   */
  default void initializeJsfService() {

  }

  /**
   * 开始服务
   */
  default void startJsfService() {

  }

  /**
   * 关闭服务
   */
  default void closeJsfService() {

  }
}
