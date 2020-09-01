package io.tdi.jsf.core.utils;

import org.springframework.util.ReflectionUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class CryptoUtils {
  /**
   * 转换为MD5
   */
  public static byte[] toMd5(byte[] data) {
    try {
      return MessageDigest.getInstance("MD5").digest(data);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Missing md5 algorithm", e);
    }
  }

  /**
   *
   */
  public static String toMd5String(byte[] data) {
    return StringUtils.toRadix16String(toMd5(data));
  }

  /**
   *
   */
  public static String toMd5String(String data) {
    return StringUtils.toRadix16String(toMd5(data.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   *
   */
  public static byte[] toSha(byte[] data) {
    try {
      return MessageDigest.getInstance("SHA-1").digest(data);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Missing sha algorithm", e);
    }
  }

  /**
   *
   */
  public static String toShaString(byte[] data) {
    return StringUtils.toRadix16String(toSha(data));
  }

  /**
   *
   */
  public static String toUniqueId(byte[] data) {
    return String.format("%s-%d", toShaString(data), data.length);
  }

  /**
   * 加密
   */
  public static byte[] encrypt(String algorithm, KeySpec keySpec, byte[] data, int offset, int length) {
    try {
      SecureRandom secureRandom = new SecureRandom();
      SecretKey secretKey = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, secureRandom);
      return cipher.doFinal(data, offset, length);
    } catch (Exception e) {
      ReflectionUtils.rethrowRuntimeException(e);
      return null;
    }
  }

  /**
   * 解密
   */
  public static byte[] decrypt(String algorithm, KeySpec keySpec, byte[] data, int offset, int length) {
    try {
      SecureRandom secureRandom = new SecureRandom();
      SecretKey secretKey = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, secureRandom);
      return cipher.doFinal(data, offset, length);
    } catch (Exception e) {
      ReflectionUtils.rethrowRuntimeException(e);
      return null;
    }
  }

  /**
   * @see #encrypt(String, KeySpec, byte[], int, int)
   */
  public static byte[] encrypt(String algorithm, KeySpec keySpec, byte[] data) {
    return encrypt(algorithm, keySpec, data, 0, data.length);
  }

  /**
   * @see #decrypt(String, KeySpec, byte[], int, int)
   */
  public static byte[] decrypt(String algorithm, KeySpec keySpec, byte[] data) {
    return decrypt(algorithm, keySpec, data, 0, data.length);
  }

  /**
   * @see #encrypt(String, KeySpec, byte[], int, int)
   */
  public static byte[] encryptByDes(byte[] key, byte[] data, int offset, int length) {
    try {
      return encrypt("DES", new DESKeySpec(key), data, offset, length);
    } catch (InvalidKeyException e) {
      ReflectionUtils.rethrowRuntimeException(e);
      return null;
    }
  }

  /**
   * @see #decrypt(String, KeySpec, byte[], int, int)
   */
  public static byte[] decryptByDes(byte[] key, byte[] data, int offset, int length) {
    try {
      return decrypt("DES", new DESKeySpec(key), data, offset, length);
    } catch (InvalidKeyException e) {
      ReflectionUtils.rethrowRuntimeException(e);
      return null;
    }
  }

  /**
   * @see #encrypt(String, KeySpec, byte[], int, int)
   */
  public static byte[] encryptByDes(byte[] key, byte[] data) {
    return encryptByDes(key, data, 0, data.length);
  }

  /**
   * @see #decrypt(String, KeySpec, byte[], int, int)
   */
  public static byte[] decryptByDes(byte[] key, byte[] data) {
    return decryptByDes(key, data, 0, data.length);
  }
}
