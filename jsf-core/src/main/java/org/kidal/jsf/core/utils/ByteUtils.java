package org.kidal.jsf.core.utils;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class ByteUtils {
  /**
   * 将byte[]转换为short
   */
  public static short toShort(byte[] data, int offset) {
    return (short) ((data[offset] & 0xff) << 8 | (data[offset + 1] & 0xff));
  }

  /**
   * 将byte[]转换为short
   */
  public static short toShort(byte[] data) {
    return toShort(data, 0);
  }

  /**
   * 将short转换为byte[]
   */
  public static byte[] fromShort(short numeric, byte[] buffer, int offset) {
    buffer[offset + 1] = (byte) (numeric);
    buffer[offset] = (byte) (numeric >> 8);

    return buffer;
  }

  /**
   * 将short转换为byte[]
   */
  public static byte[] fromShort(short numeric) {
    return fromShort(numeric, new byte[2], 0);
  }

  /**
   * 将byte[]转换为int
   */
  public static int toInt(byte[] data, int offset) {
    return ((data[offset] & 0xff) << 24 |
      (data[offset + 1] & 0xff) << 16 |
      (data[offset + 2] & 0xff) << 8 |
      (data[offset + 3] & 0xff));
  }

  /**
   * 将byte[]转换为int
   */
  public static int toInt(byte[] data) {
    return toInt(data, 0);
  }

  /**
   * 将int转换为byte[]
   */
  public static byte[] fromInt(int numeric, byte[] buffer, int offset) {
    buffer[offset + 3] = (byte) (numeric);
    buffer[offset + 2] = (byte) (numeric >> 8);
    buffer[offset + 1] = (byte) (numeric >> 16);
    buffer[offset] = (byte) (numeric >> 24);

    return buffer;
  }

  /**
   * 将int转换为byte[]
   */
  public static byte[] fromInt(int numeric) {
    return fromInt(numeric, new byte[4], 0);
  }

  /**
   * 将byte[]转换为long
   */
  public static long toLong(byte[] data, int offset) {
    return ((long) (data[offset] & 0xff) << 56 |
      (long) (data[offset + 1] & 0xff) << 48 |
      (long) (data[offset + 2] & 0xff) << 40 |
      (long) (data[offset + 3] & 0xff) << 32 |
      (long) (data[offset + 4] & 0xff) << 24 |
      (long) (data[offset + 5] & 0xff) << 16 |
      (long) (data[offset + 6] & 0xff) << 8 |
      (long) (data[offset + 7] & 0xff));
  }

  /**
   * 将byte[]转换为long
   */
  public static long toLong(byte[] data) {
    return toLong(data, 0);
  }

  /**
   * 将long转换为byte[]
   */
  public static byte[] fromLong(long numeric, byte[] buffer, int offset) {
    buffer[offset + 7] = (byte) (numeric);
    buffer[offset + 6] = (byte) (numeric >> 8);
    buffer[offset + 5] = (byte) (numeric >> 16);
    buffer[offset + 4] = (byte) (numeric >> 24);
    buffer[offset + 3] = (byte) (numeric >> 32);
    buffer[offset + 2] = (byte) (numeric >> 40);
    buffer[offset + 1] = (byte) (numeric >> 48);
    buffer[offset] = (byte) (numeric >> 56);

    return buffer;
  }

  /**
   * 将long转换为byte[]
   */
  public static byte[] fromLong(long numeric) {
    return fromLong(numeric, new byte[8], 0);
  }

  /**
   * 将16进制字符串转换为byte[]
   */
  public static byte[] fromHex(String data) {
    final int two = 2;
    final byte[] bytes = data.getBytes();

    if ((bytes.length % two) != 0) {
      throw new IllegalArgumentException("Given data's length must be even");
    }

    final byte[] buffer = new byte[bytes.length / two];

    for (int i = 0; i < bytes.length; i += two) {
      buffer[i / 2] = Byte.parseByte(new String(bytes, i, two), 16);
    }

    return buffer;
  }
}
