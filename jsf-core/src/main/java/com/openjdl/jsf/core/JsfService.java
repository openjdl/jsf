package com.openjdl.jsf.core;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-04 23:15:34
 *
 * @author kidal
 * @since 0.1.0
 */
public interface JsfService {
  /**
   * 获取模块ID
   *
   * @return 模块ID.
   */
  @NotNull
  String getJsfServiceName();

  /**
   *
   */
  default void registerSelf() {
    JsfMicroService.register(this);
  }

  /**
   * 初始化阶段
   */
  default void onJsfServiceInitializeStage() throws Exception {

  }

  /**
   * 注入阶段
   */
  default void onJsfServiceInjectStage() throws Exception {

  }

  /**
   * 开始服务阶段
   */
  default void onJsfServiceStartStage() throws Exception {

  }

  /**
   * 关闭阶段
   */
  default void onJsfServiceCloseStage() throws Exception {

  }
}
