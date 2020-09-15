package com.openjdl.jsf.core.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class IDUtils {
  /**
   *
   */
  public static String randomUniqueId() {
    UUID uuid = UUID.randomUUID();
    long mostSignificantBits = uuid.getMostSignificantBits();
    long leastSignificantBits = uuid.getLeastSignificantBits();

    String part0 = StringUtils.toRadix62String(mostSignificantBits, '0');
    String part1 = StringUtils.toRadix62String(leastSignificantBits, '0');

    return String.format("%s_%s", part0, part1);
  }

  /**
   *
   */
  public static String randomUniqueId2() {
    UUID uuid = UUID.randomUUID();
    long mostSignificantBits = uuid.getMostSignificantBits();
    long leastSignificantBits = uuid.getLeastSignificantBits();

    String part0 = StringUtils.toRadix62String(mostSignificantBits, '0');
    String part1 = StringUtils.toRadix62String(leastSignificantBits, '0');

    StringBuilder sb = new StringBuilder(32);
    padZeroChars(sb, 12 - part0.length());
    sb.append(part0);
    padZeroChars(sb, 12 - part1.length());
    sb.append(part1);
    return sb.toString();
  }

  /**
   *
   */
  public static String randomUniqueId3(String confusion) {
    return randomUniqueId2() + confusion;
  }

  /**
   *
   */
  public static String randomUniqueId4() {
    return randomUniqueId3(StringUtils.toRadix62String(System.currentTimeMillis()));
  }

  /**
   *
   */
  private static void padZeroChars(StringBuilder sb, int num) {
    for (int i = 0; i < num; i++) {
      sb.append('0');
    }
  }

  /**
   *
   */
  public static String getEnvironmentPrefix(String profile) {
    switch (profile) {
      // 9字头：开发
      case SpringUtils.PROFILE_DEV:
        return "9";
      // 8字头：对内发布预览
      case SpringUtils.PROFILE_RC:
        return "8";
      // 2字头：生产环境
      case SpringUtils.PROFILE_PROD:
        // 1字头：未知
      default:
        return "1";
    }
  }

  /**
   *
   */
  @NotNull
  public static String getPrefix(String profile) {
    return getEnvironmentPrefix(profile) + "0";
  }

  /**
   *
   */
  @NotNull
  public static String getBySerial(String profile, long serial, int digitCount) {
    Calendar c = Calendar.getInstance();
    String prefix = getPrefix(profile);
    String date = String.format("%02d%02d%02d%02d",
      c.get(Calendar.YEAR) - 2000, c.get(Calendar.MONTH) + 1,
      c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY)
    );
    return String.format(String.format("%%s%%s%%0%dd", digitCount), prefix, date, serial);
  }

  /**
   *
   */
  @NotNull
  public static String getBySerial(@NotNull SpringUtils springUtils, long serial, int digitCount) {
    String[] activeProfiles = springUtils.getEnvironment().getActiveProfiles();
    String profile = activeProfiles.length > 1 ? activeProfiles[0] : "dev";
    return getBySerial(profile, serial, digitCount);
  }
}
