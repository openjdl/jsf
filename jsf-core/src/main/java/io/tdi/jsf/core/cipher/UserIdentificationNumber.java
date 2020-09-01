package io.tdi.jsf.core.cipher;

import com.google.common.base.Objects;
import io.tdi.jsf.core.exception.JsfException;
import io.tdi.jsf.core.exception.JsfExceptions;
import io.tdi.jsf.core.utils.CryptoUtils;
import io.tdi.jsf.core.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created at 2020-08-05 17:00:29
 * 用户身份识别码
 * <pre>
 * 格式:
 *      「版本号」-「识别码正文」
 *      例如:
 *          jsf1&4&999999&a1eZQmDxxA0_08wslLM3AvcF
 * 版本:
 *      1:
 *          jsf1&category&uin&[Base64(DES(secret))]
 * </pre>
 *
 * @author kidal
 * @since 0.1.0
 */
public class UserIdentificationNumber implements Serializable {
  /**
   *
   */
  @NotNull
  public static String UIN_SECRET_KEY = "";

  /**
   * 尝试解析
   */
  public static UserIdentificationNumber tryParse(String source) {
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
  @NotNull
  public static UserIdentificationNumber parse(String source) {
    if (StringUtils.isEmpty(source)) {
      throw new JsfException(JsfExceptions.INCORRECT_UIN);
    }

    try {
      String[] parts = source.split("&", 2);

      if (parts.length < 2 || StringUtils.isAnyEmpty(parts[0], parts[1])) {
        throw new JsfException(JsfExceptions.INCORRECT_UIN);
      }

      // parse
      String version = parts[0];
      String text = parts[1];

      if ("jsf1".equals(version)) {
        return parse1(text);
      }

      throw new JsfException(JsfExceptions.INCORRECT_UIN);
    } catch (JsfException e) {
      throw e;
    } catch (Exception e) {
      throw new JsfException(JsfExceptions.INCORRECT_UIN, e);
    }
  }

  /**
   * 解析第一版
   */
  @NotNull
  private static UserIdentificationNumber parse1(@NotNull String text) {
    // split
    String[] parts = text.split("&", 3);

    if (parts.length < 2 || StringUtils.isAnyEmpty(parts[0], parts[1], parts[2])) {
      throw new JsfException(JsfExceptions.INCORRECT_UIN);
    }

    String category = parts[0];
    String uin = parts[1];
    String secretBase64 = parts[2];

    // token
    byte[] encryptedSecret = Base64.decodeBase64(secretBase64);
    byte[] secretBytes = CryptoUtils.decryptByDes(UIN_SECRET_KEY.getBytes(), encryptedSecret);
    String secret = new String(secretBytes);

    UserIdentificationNumber passport = new UserIdentificationNumber();
    passport.setCategory(category);
    passport.setUserId(uin);
    passport.setSecret(secret);
    return passport;
  }

  /**
   * 分类
   */
  @NotNull
  private String category = StringUtils.EMPTY;

  /**
   * 用户身份识别码
   */
  @NotNull
  private String userId = StringUtils.EMPTY;

  /**
   * 原始密文
   */
  @NotNull
  private String secret = StringUtils.EMPTY;

  /**
   *
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserIdentificationNumber that = (UserIdentificationNumber) o;
    return Objects.equal(category, that.category) &&
      Objects.equal(userId, that.userId) &&
      Objects.equal(secret, that.secret);
  }

  /**
   *
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(category, userId, secret);
  }

  /**
   *
   */
  @Override
  public String toString() {
    return getString1();
  }

  /**
   * 获取第四版字符串
   */
  public String getString1() {
    byte[] encryptedSecret = CryptoUtils.encryptByDes(UIN_SECRET_KEY.getBytes(), secret.getBytes());
    String secretBase64 = Base64.encodeBase64URLSafeString(encryptedSecret);
    return String.format("jsf1&%s&%s&%s", category, userId, secretBase64);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  public String getCategory() {
    return category;
  }

  public void setCategory(@NotNull String category) {
    this.category = category;
  }

  @NotNull
  public String getUserId() {
    return userId;
  }

  public void setUserId(@NotNull String userId) {
    this.userId = userId;
  }

  @NotNull
  public String getSecret() {
    return secret;
  }

  public void setSecret(@NotNull String secret) {
    this.secret = secret;
  }
}
