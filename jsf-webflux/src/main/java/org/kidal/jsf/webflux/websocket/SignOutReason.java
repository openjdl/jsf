package org.kidal.jsf.webflux.websocket;

/**
 * Created at 2020-08-12 13:49:56
 *
 * @author kidal
 * @since 0.1.0
 */
public enum SignOutReason {
  /**
   * 正常登出
   */
  NORMAL("NORMAL"),
  /**
   * 切换账号
   */
  SWITCH("SWITCH"),
  /**
   * 在其他地方登录
   */
  ELSEWHERE("ELSEWHERE"),
  /**
   * 会话关闭
   */
  CLOSE("CLOSE"),
  //
  ;

  private final String value;

  SignOutReason(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
