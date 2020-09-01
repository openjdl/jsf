package io.tdi.jsf.core.utils;

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
      // 3字头：生产环境
      case SpringUtils.PROFILE_PROD:
        return "3";
      // 2字头：不要使用（1.0系统占用）
      case "DO_NOT_USE":
        return "2";
      // 1字头：未知
      default:
        return "1";
    }
  }

  /**
   *
   */
  public static String getPrefix(String profile) {
    return getEnvironmentPrefix(profile) + "01";
  }

  /**
   *
   */
  public static String getBySerial(String profile, long serial, int digitCount) {
    String format = String.format("%%s%%%02dd", digitCount);
    return String.format(format, getPrefix(profile), serial);
  }

  /**
   *
   */
  public static String getBySerial(SpringUtils springUtils, long serial) {
    String[] activeProfiles = springUtils.getEnvironment().getActiveProfiles();
    String profile = activeProfiles.length > 1 ? activeProfiles[0] : "dev";
    return getBySerial(profile, serial, 8);
  }
}
