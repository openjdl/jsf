package org.kidal.jsf.core;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-04 23:15:34
 *
 * @author kidal
 * @since 0.1.0
 */
public interface JsfMicroServiceModule {
  /**
   * 获取模块ID
   *
   * @return 模块ID.
   */
  @NotNull
  String getJsfMicroServiceModuleName();

  /**
   * 初始化模块.
   */
  default void initializeJsfMicroServiceModule() {

  }

  /**
   * 开始模块
   */
  default void startJsfMicroServiceModule() {

  }

  /**
   * 关闭模块
   */
  default void closeJsfMicroServiceModule() {

  }
}
