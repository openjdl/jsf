package io.tdi.jsf.core.cipher;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-05 17:00:29
 *
 * @author kidal
 * @since 0.1.0
 */
public enum UserPassportScope {
  /**
   * 未知范围授权
   */
  UNKNOWN(0, "Z8kr1234rL6X2dSn"),

  /**
   * 对外SDK范围授权
   */
  SDK(10, "3321HuXOyz9hxiOx"),

  /**
   * 对内API范围授权
   */
  API(20, "b9RUqhN4O2322MxGw")

  //
  ;

  /**
   * 尝试解析通信证范围
   */
  public static UserPassportScope tryParse(String source) {
    if (source == null) {
      return UNKNOWN;
    }
    try {
      return UserPassportScope.valueOf(source);
    } catch (IllegalArgumentException e) {
      return UNKNOWN;
    }
  }

  /**
   *
   */
  private final int level;

  /**
   *
   */
  private final String desKey;

  /**
   *
   */
  UserPassportScope(int level, @NotNull String desKey) {
    this.level = level;
    this.desKey = desKey;
  }

  /**
   *
   */
  public int getLevel() {
    return level;
  }

  /**
   *
   */
  public String getDesKey() {
    return desKey;
  }
}
