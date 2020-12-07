package com.openjdl.jsf.webflux.modbus.dtu;

/**
 * Created at 2020-12-07 23:40:13
 *
 * @author kidal
 * @since 0.5
 */
public enum ModbusDtuSignOutReason {
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
  //
  ;
}
