package org.kidal.jsf.core.utils;

import java.util.Arrays;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class PasswordUtils {
  /**
   * 比较密码
   *
   * @param password 密码(明码)
   * @param cipher   密码(暗号)
   */
  public static boolean comparePassword(String password, String cipher) {
    if (password == null) {
      throw new IllegalArgumentException("Given password is incorrect");
    }
    if (cipher == null) {
      throw new IllegalArgumentException("Given cipher is incorrect");
    }

    // get meta
    if (cipher.charAt(0) != '{') {
      throw new IllegalArgumentException("Given cipher is incorrect");
    }
    int metaEndIndex = cipher.indexOf('}');
    if (metaEndIndex == -1) {
      throw new IllegalArgumentException("Given cipher is incorrect");
    }
    String meta = cipher.substring(1, metaEndIndex);

    // resolve meta
    String[] metaStrings = meta.split(":");
    if (metaStrings.length != 3) {
      throw new IllegalArgumentException("Given cipher is incorrect");
    }
    String id = metaStrings[0];
    String type = metaStrings[1];
    String version = metaStrings[2];
    if (!"password".equals(id)) {
      throw new IllegalArgumentException("Given cipher is incorrect");
    }

    // hash password
    String hashedPassword = null;
    switch (type) {
      case "clearly":
        if ("0".equals(version)) {
          hashedPassword = hashPasswordClearly0(password);
        }
        break;
      case "cipher":
        if ("1".equals(version)) {
          hashedPassword = hashPasswordCipher1(password);
        } else if ("2".equals(version)) {
          hashedPassword = hashPasswordCipher2(password);
        }
        break;
      default:
        break;
    }
    if (hashedPassword == null) {
      throw new IllegalArgumentException("Given cipher is incorrect");
    }

    // compare
    return hashedPassword.equals(cipher);
  }

  /**
   * 比较密码
   *
   * @param password 密码(明码)
   * @param cipher   密码(暗号)
   */
  public static boolean tryComparePassword(String password, String cipher) {
    try {
      return comparePassword(password, cipher);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   *
   */
  public static String hashPasswordClearly0(String password) {
    return "{password:clearly:0}" + password;
  }

  /**
   *
   */
  public static String hashPasswordCipher1(String password) {
    byte[] b0 = CryptoUtils.toMd5(password.getBytes());
    String s0 = StringUtils.toRadix16String(b0);
    Arrays.sort(b0);
    String s1 = StringUtils.toRadix16String(b0);
    String s2 = s0 + password + s1;

    return "{password:cipher:1}" + CryptoUtils.toMd5String(s2.getBytes());
  }

  /**
   *
   */
  public static String hashPasswordCipher2(String password) {
    byte[] b0 = CryptoUtils.toSha(password.getBytes());
    String s0 = StringUtils.toRadix16String(b0);
    Arrays.sort(b0);
    String s1 = StringUtils.toRadix16String(b0);
    String s2 = s0 + password + s1;

    return "{password:cipher:2}" + CryptoUtils.toShaString(s2.getBytes());
  }
}
