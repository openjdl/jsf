package com.openjdl.jsf.webflux.socket;

/**
 * Created at 2020-12-24 14:54:14
 *
 * @author zink
 * @since 2.0.0
 */
public enum SocketSignOutReason {
  /**
   * 正常登出
   */
  NORMAL,
  /**
   * 切换账号
   */
  SWITCH,
  /**
   * 在其他地方登录
   */
  ELSEWHERE,
  /**
   * 会话关闭
   */
  CLOSE,
}
