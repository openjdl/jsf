package org.kidal.jsf.core.utils;

import java.util.Stack;

/**
 * Created by kidal on 2017/6/28.
 *
 * @author kidal
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
  /**
   *
   */
  public static final String MAX_RADIX_62_LONG = "AzL8n0Y58m8";

  /**
   * 62进制编码
   */
  @SuppressWarnings("SpellCheckingInspection")
  private static final char[] RADIX_62_CHAR_ARRAY = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

  /**
   * 字符串是否介于指定支付之间
   */
  public static boolean between(final CharSequence str, final CharSequence prefix, final CharSequence suffix) {
    return startsWith(str, prefix) && endsWith(str, suffix);
  }

  /**
   * 检测字符串是几个字节的UTF8编码形式
   *
   * @return 0: 不是UTF8字符串; 1: UTF8; 2: UTF8MB2; 3: UTF8MB3; 4: UTF8MB4.
   */
  public static int testUtf8ByteCount(byte[] input) {
    if (input == null || input.length == 0) {
      return 0;
    }

    int mask = 0;

    for (int i = 0; i < input.length; i++) {
      byte b = input[i];

      if ((b & 0xf0) == 0xf0) {
        mask |= 16;
        i += 3;
        continue;
      }

      if ((b & 0xe0) == 0xe0) {
        mask |= 8;
        i += 2;
        continue;
      }

      if ((b & 0xc0) == 0xc0) {
        mask |= 4;
        i += 1;
        continue;
      }

      if ((b >> 7) == 0) {
        mask |= 2;
      }
    }

    if ((mask & 16) == 16) {
      return 4;
    } else if ((mask & 8) == 8) {
      return 3;
    } else if ((mask & 4) == 4) {
      return 2;
    } else if ((mask & 2) == 2) {
      return 1;
    }

    return 0;
  }

  /**
   * @see #testUtf8ByteCount(byte[])
   */
  public static int testUtf8ByteCount(String input) {
    if (input == null || input.length() == 0) {
      return 0;
    }

    return testUtf8ByteCount(input.getBytes());
  }

  /**
   * 转换为16进制字符串
   */
  private static String toRadix16String(byte[] input, int offset, int length) {
    StringBuilder builder = new StringBuilder();

    for (int i = offset; i < offset + length; i++) {
      String temporary = Integer.toHexString(input[i] & 0xff);

      if (temporary.length() == 1) {
        builder.append('0');
      }

      builder.append(temporary);
    }

    return builder.toString();
  }

  /**
   * @see #toRadix16String(byte[], int, int)
   */
  public static String toRadix16String(byte[] input) {
    return toRadix16String(input, 0, input.length);
  }

  /**
   *
   */
  public static String fixRadix33Similar(String input) {
    return input
      .replaceAll("i", "x")
      .replaceAll("l", "y")
      .replaceAll("o", "z")
      .replaceAll("I", "X")
      .replaceAll("L", "Y")
      .replaceAll("O", "Z");
  }

  /**
   *
   */
  public static String unfixRadix33Similar(String input) {
    return input
      .replaceAll("x", "i")
      .replaceAll("y", "l")
      .replaceAll("z", "o")
      .replaceAll("X", "I")
      .replaceAll("Y", "L")
      .replaceAll("Z", "O");
  }

  /**
   * 将数字转换为33进制字符串
   */
  public static String toRadix33String(long input) {
    return fixRadix33Similar(Long.toString(input, 33));
  }

  /**
   * 将33进制字符串转换为数字
   */
  public static long fromRadix33String(String input) {
    return Long.parseLong(unfixRadix33Similar(input));
  }

  /**
   *
   */
  public static String toRadix62String(long number, char negativeSymbol) {
    // min: -AzL8n0Y58m8
    // max: +aZl8N0y58M7
    if (number == 0) {
      return "0";
    } else if (number == Long.MIN_VALUE) {
      return negativeSymbol + "AzL8n0Y58m8";
    }
    boolean negative = number < 0;
    long value = negative ? -number : number;

    Stack<Character> characterStack = new Stack<>();
    StringBuilder builder = new StringBuilder();

    while (value != 0) {
      characterStack.add(RADIX_62_CHAR_ARRAY[Long.valueOf(value - (value / 62) * 62).intValue()]);
      value = value / 62;
    }

    if (negative) {
      builder.append(negativeSymbol);
    }

    while (!characterStack.isEmpty()) {
      builder.append(characterStack.pop());
    }

    return builder.toString();
  }

  /**
   *
   */
  public static String toRadix62String(long number) {
    return toRadix62String(number, '-');
  }

  /**
   *
   */
  public static long fromRadix62String(String value, char negativeSymbol) {
    if (value == null || value.length() == 0) {
      throw new IllegalArgumentException("Given value is null or empty");
    }

    if (value.charAt(0) == negativeSymbol) {
      if (value.length() == 1) {
        throw new IllegalArgumentException("Given value is incorrect");
      }

      if (value.charAt(1) == '0') {
        return 0L;
      }

      if (value.length() == 12 && value.substring(1).equals(MAX_RADIX_62_LONG)) {
        return Long.MIN_VALUE;
      }
    } else if (value.charAt(0) == '0') {
      return 0L;
    }

    boolean negative = value.charAt(0) == negativeSymbol;
    if (negative) {
      value = value.substring(1);
    }

    int limit = value.length();
    long number = find(value.charAt(0));

    for (int i = 1; i < limit; i++) {
      number = 62 * number + find(value.charAt(i));
    }

    return negative ? -number : number;
  }

  /**
   *
   */
  public static long fromRadix62String(String value) {
    return fromRadix62String(value, '-');
  }

  /**
   *
   */
  private static int find(char n) {
    for (int i = 0; i < RADIX_62_CHAR_ARRAY.length; i++) {
      if (RADIX_62_CHAR_ARRAY[i] == n) {
        return i;
      }
    }
    return -1;
  }

  /**
   *
   */
  public static String prepend(String input, char ch, int length) {
    if (length > input.length()) {
      StringBuilder sb = new StringBuilder();
      int count = length - input.length();
      for (int i = 0; i < count; i++) {
        sb.append(ch);
      }
      sb.append(input);
      return sb.toString();
    }
    return input;
  }

  /**
   *
   */
  public static String lowercaseAndSplitWith(String in, String separator) {
    if (in == null || in.length() == 0) {
      return in;
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < in.length(); i++) {
      char ch = in.charAt(i);

      if (Character.isUpperCase(ch)) {
        if (i == 0) {
          sb.append(Character.toLowerCase(ch));
        } else {
          sb.append(separator).append(Character.toLowerCase(ch));
        }
      } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  /**
   *
   */
  public static String orElse(String in, String def) {
    return in != null ? in : def;
  }

  /**
   *
   */
  public static boolean isHttpUrl(String s) {
    return s != null && startsWithAny(s, "//", "http://", "https://");
  }

  /**
   *
   */
  public static boolean isNotHttpUrl(String s) {
    return !isHttpUrl(s);
  }

  /**
   *
   */
  public static String removeHttpProtocol(String s) {
    if (s == null) {
      return null;
    } else if (s.startsWith("//")) {
      return s;
    } else if (s.startsWith("http:")) {
      return s.substring("http:".length());
    } else if (s.startsWith("https:")) {
      return s.substring("https:".length());
    } else {
      return s;
    }
  }

  /**
   *
   */
  public static String removeHttpPrefix(String s) {
    if (s == null) {
      return null;
    } else if (s.startsWith("//")) {
      return s.substring("//".length());
    } else if (s.startsWith("http://")) {
      return s.substring("http://".length());
    } else if (s.startsWith("https://")) {
      return s.substring("https://".length());
    } else {
      return s;
    }
  }
}
