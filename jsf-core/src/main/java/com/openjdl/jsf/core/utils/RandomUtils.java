package com.openjdl.jsf.core.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class RandomUtils {
  public static final SecureRandom DEFAULT_RANDOM = new SecureRandom();
  public static final String UPPER_LOWER_RANDOM_STRING_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String UPPER_RANDOM_STRING_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String LOWER_RANDOM_STRING_CHARS = "abcdefghijklmnopqrstuvwxyz";
  public static final String DEFAULT_RANDOM_STRING_CHARS = UPPER_LOWER_RANDOM_STRING_CHARS;

  /**
   *
   */
  public static int nextInt(Random random, int min, int max) {
    if (min > max) {
      throw new IllegalArgumentException("Given min is greater than max");
    } else if (min == max) {
      throw new IllegalArgumentException("Given min is equals max");
    }

    return (int) (min + random.nextDouble() * (max - min));
  }

  /**
   *
   */
  public static int nextInt(int min, int max) {
    return nextInt(DEFAULT_RANDOM, min, max);
  }

  /**
   *
   */
  public static String nextString(int length, String chars) {
    StringBuilder sb = new StringBuilder();
    int len = chars.length();
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt((int) Math.round(Math.random() * (len - 1))));
    }
    return sb.toString();
  }

  /**
   *
   */
  public static String nextString(int length) {
    return nextString(length, DEFAULT_RANDOM_STRING_CHARS);
  }
}
