package org.kidal.jsf.core.cipher;

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.exception.JsfExceptions;
import org.kidal.jsf.core.utils.CryptoUtils;
import org.kidal.jsf.core.utils.StringUtils;

import java.io.Serializable;

/**
 * Created at 2020-08-05 17:00:29
 * <pre>
 * 格式:
 *      版本-通行证文本
 *      例如:
 *          1-4-999999-a1eZQmDxxA0_08wslLM3AvcF
 * 版本:
 *      1:  应用局部用户Id
 *          1-uin-scope-[Base64(DES(accessToken) by KEY)]
 * </pre>
 *
 * @author kidal
 * @since 0.1.0
 */
public class UserPassport implements Serializable {
  /**
   * 原始密钥
   */
  private String token = StringUtils.EMPTY;

  /**
   * 用户身份识别码
   */
  private long uin = 0L;
  private UserPassportScope scope;

  /**
   * 尝试解析
   */
  public static UserPassport tryParse(String source) {
    if (source == null || source.length() == 0) {
      return null;
    }
    try {
      return parse(source);
    } catch (Exception ignored) {
      return null;
    }
  }

  /**
   * 解析
   */
  public static UserPassport parse(String s) {
    if (StringUtils.isEmpty(s)) {
      throw new JsfException(JsfExceptions.INCORRECT_PASSPORT);
    }

    try {
      String[] parts = s.split("-", 2);
      if (parts.length < 2 || StringUtils.isAnyEmpty(parts[0], parts[1])) {
        throw new JsfException(JsfExceptions.INCORRECT_PASSPORT);
      }

      // parse
      String version = parts[0];
      String text = parts[1];

      if ("1".equals(version)) {
        return parse1(text);
      }

      throw new JsfException(JsfExceptions.INCORRECT_PASSPORT);
    } catch (JsfException e) {
      throw e;
    } catch (Exception e) {
      throw new JsfException(JsfExceptions.INCORRECT_PASSPORT, e);
    }
  }

  /**
   * 转换为字符串
   */
  @Override
  public String toString() {
    return getString1();
  }

  /**
   * 解析第一版
   */
  @NotNull
  private static UserPassport parse1(@NotNull String text) {
    // split
    String[] parts = text.split("-", 3);
    if (parts.length < 2 || StringUtils.isAnyEmpty(parts[0], parts[1], parts[2])) {
      throw new JsfException(JsfExceptions.INCORRECT_PASSPORT);
    }

    // uin
    long uin = Long.parseLong(parts[0]);
    // scope
    UserPassportScope scope = UserPassportScope.tryParse(parts[1]);
    String desKey = scope.getDesKey();
    // token
    byte[] encryptedAccessToken = Base64.decodeBase64(parts[2]);
    byte[] accessTokenBytes = CryptoUtils.decryptByDes(desKey.getBytes(), encryptedAccessToken);
    String accessToken = new String(accessTokenBytes);

    UserPassport passport = new UserPassport();
    passport.setUin(uin);
    passport.setToken(accessToken);
    passport.setScope(scope);
    return passport;
  }

  /**
   * 获取第四版字符串
   */
  public String getString1() {
    String desKey = scope.getDesKey();
    byte[] encryptedAccessTokenBytes = CryptoUtils.encryptByDes(desKey.getBytes(), token.getBytes());
    String encodedAccessToken = Base64.encodeBase64URLSafeString(encryptedAccessTokenBytes);
    return String.format("4-%d-%s-%s", uin, scope.name(), encodedAccessToken);
  }

  /**
   *
   */
  public String getToken() {
    return token;
  }

  /**
   *
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   *
   */
  public long getUin() {
    return uin;
  }

  /**
   *
   */
  public void setUin(long uin) {
    this.uin = uin;
  }

  /**
   *
   */
  public UserPassportScope getScope() {
    return scope;
  }

  /**
   *
   */
  public void setScope(UserPassportScope scope) {
    this.scope = scope;
  }
}
