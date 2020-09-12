package com.openjdl.jsf.core;

/**
 * Created at 2020-08-04 23:03:49
 *
 * @author kidal
 * @since 0.1.0
 */
public interface JsfMicroServiceListener {
  /**
   *
   */
  default void registerSelf() {
    JsfMicroService.listen(this);
  }

  /**
   *
   */
  default void deregisterSelf() {
    JsfMicroService.deaf(this);
  }

  /**
   * 全部服务初始化完成
   */
  default void onMicroServiceInitialized() throws Exception {

  }

  /**
   * 全部服务注入完成
   */
  default void onMicroServiceInjected() throws Exception {

  }

  /**
   * 全部服务启动完成
   */
  default void onMicroServiceStarted() throws Exception {

  }

  /**
   * 全部服务关闭
   */
  default void onMicroServiceClosed() throws Exception {

  }
}
