package io.tdi.jsf.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class MathUtils {
  /**
   *
   */
  public static <T extends Comparable<T>> T clamp(T count, T min, T max) {
    return count.compareTo(min) < 0 ? min : (count.compareTo(max) > 0 ? max : count);
  }

  /**
   *
   */
  public static double divideAndRound(double a, double b, int scale, RoundingMode roundingMode) {
    return new BigDecimal(a).divide(new BigDecimal(b), scale, roundingMode).doubleValue();
  }

  /**
   *
   */
  public static double divideAndRound(double a, double b, int scale) {
    return new BigDecimal(a).divide(new BigDecimal(b), scale, RoundingMode.HALF_UP).doubleValue();
  }

  /**
   *
   */
  public static double divideAndRoundUp(double a, double b, int scale) {
    return divideAndRound(a, b, scale, RoundingMode.UP);
  }

  /**
   *
   */
  public static double divideAndRoundDown(double a, double b, int scale) {
    return divideAndRound(a, b, scale, RoundingMode.DOWN);
  }

  /**
   *
   */
  public static double round(double value, int scale, RoundingMode roundingMode) {
    return divideAndRound(value, 1, scale, roundingMode);
  }

  /**
   *
   */
  public static double round(double value, int scale) {
    return round(value, scale, RoundingMode.HALF_UP);
  }

  /**
   *
   */
  public static double roundUp(double value, int scale) {
    return round(value, scale, RoundingMode.UP);
  }

  /**
   *
   */
  public static double roundDown(double value, int scale) {
    return round(value, scale, RoundingMode.DOWN);
  }
}
