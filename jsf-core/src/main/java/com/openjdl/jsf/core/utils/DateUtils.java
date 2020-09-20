package com.openjdl.jsf.core.utils;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
  public static final String PATTERN_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String PATTERN_DATE = "yyyy-MM-dd";
  public static final String PATTERN_TIME = "HH:mm:ss";
  public static final String PATTERN_SHORT_TIME = "HH:mm";
  public static final Date LONG_BEFORE_TIME = toDate("1970-01-01 00:00:00", PATTERN_DATE_TIME);
  public static final Date LONG_AFTER_TIME = toDate("2048-01-01 00:00:00", PATTERN_DATE_TIME);

  /**
   *
   */
  public static String toString(Date a, String pattern) {
    return new SimpleDateFormat(pattern).format(a);
  }

  /**
   *
   */
  public static String toString(long a, String pattern) {
    return toString(new Date(a), pattern);
  }

  /**
   *
   */
  public static String toString(Date a) {
    return toString(a, PATTERN_DATE_TIME);
  }

  /**
   *
   */
  public static String toString(long a) {
    return toString(a, PATTERN_DATE_TIME);
  }

  /**
   *
   */
  public static String toStringSafely(Object a, String pattern) {
    if (a == null) {
      return null;
    } else if (a instanceof Date) {
      return new SimpleDateFormat(pattern).format(a);
    } else if (a.getClass() == Long.class) {
      return new SimpleDateFormat(pattern).format(new Date((long) a));
    } else {
      return null;
    }
  }

  /**
   *
   */
  public static String toStringSafely(Object a) {
    return toStringSafely(a, PATTERN_DATE_TIME);
  }

  /**
   *
   */
  public static String toStringSafelyWithTimeZone(Object a, String pattern, String tz) {
    if (a == null) {
      return null;
    } else if (a instanceof Date) {
      SimpleDateFormat sdf = new SimpleDateFormat(pattern);
      if (tz != null) {
        sdf.setTimeZone(TimeZone.getTimeZone(tz));
      }
      return sdf.format(a);
    } else if (a.getClass() == Long.class) {
      SimpleDateFormat sdf = new SimpleDateFormat(pattern);
      if (tz != null) {
        sdf.setTimeZone(TimeZone.getTimeZone(tz));
      }
      return sdf.format(new Date((long) a));
    } else {
      return null;
    }
  }

  /**
   *
   */
  public static String toStringSafelyWithTimeZone(Object a, String tz) {
    return toStringSafelyWithTimeZone(a, PATTERN_DATE_TIME, null);
  }

  /**
   *
   */
  public static Date toDate(String time, String pattern) {
    try {
      return new SimpleDateFormat(pattern).parse(time);
    } catch (ParseException e) {
      throw new IllegalArgumentException("参数 time " + time + " 不是正确的时间格式", e);
    }
  }

  /**
   *
   */
  public static Date iso8601ToDate(String time) {
    try {
      TimeZone tz = TimeZone.getTimeZone("GMT");
      Calendar c = Calendar.getInstance(tz);
      SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_ISO8601);

      sdf.setCalendar(c);
      c.setTime(sdf.parse(time));

      return c.getTime();
    } catch (ParseException e) {
      throw new IllegalArgumentException("参数 time " + time + " 不是正确的时间格式", e);
    }
  }

  /**
   *
   */
  public static String iso8601ToString(Date time) {
    TimeZone tz = TimeZone.getTimeZone("GMT");
    Calendar c = Calendar.getInstance(tz);
    SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_ISO8601);

    sdf.setCalendar(c);
    return sdf.format(time);
  }

  /**
   *
   */
  public static String iso8601ToStringSafely(Object time) {
    if (time == null) {
      return null;
    } else if (time instanceof Date) {
      return iso8601ToString((Date) time);
    } else if (time.getClass() == Long.class) {
      return iso8601ToString(new Date((Long) time));
    } else {
      return null;
    }
  }

  /**
   *
   */
  public static Date uncertainToDateSafely(String s) {
    if (s == null || s.isEmpty()) {
      return null;
    }

    // gmt
    try {
      return iso8601ToDate(s);
    } catch (IllegalArgumentException ignored) {

    }

    // datetime
    try {
      return toDate(s, PATTERN_DATE_TIME);
    } catch (IllegalArgumentException ignored) {

    }

    // date
    try {
      return toDate(s, PATTERN_DATE);
    } catch (IllegalArgumentException ignored) {

    }

    // datetime
    try {
      return toDate(s, "yyyy/MM/dd HH:mm:ss");
    } catch (IllegalArgumentException ignored) {

    }

    // date
    try {
      return toDate(s, "yyyy/MM/dd");
    } catch (IllegalArgumentException ignored) {

    }

    // not correct
    return null;
  }

  /**
   *
   */
  public static Date utcToZone(Date time, String tzId) {
    TimeZone gmtTz = TimeZone.getTimeZone("UTC");
    TimeZone tz = TimeZone.getTimeZone(tzId);
    long millis = time.getTime() - gmtTz.getRawOffset() + tz.getRawOffset();
    return new Date(millis);
  }

  /**
   *
   */
  public static Date getFirstTimeOfDay(Date a) {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(a.getTime());
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }

  /**
   *
   */
  @NotNull
  public static Date getFirstTimeOfNextDay(@NotNull Date a) {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(a.getTime());
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    c.add(Calendar.DAY_OF_MONTH, 1);
    return c.getTime();
  }

  /**
   *
   */
  @NotNull
  public static Date getLastTimeOfDay(@NotNull Date a) {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(a.getTime());
    c.set(Calendar.HOUR_OF_DAY, 23);
    c.set(Calendar.MINUTE, 59);
    c.set(Calendar.SECOND, 59);
    c.set(Calendar.MILLISECOND, 999);
    return c.getTime();
  }

  /**
   *
   */
  @NotNull
  public static DateIntervals calculateIntervals(@NotNull Date a, @NotNull Date b) {
    return calculateIntervals(a.getTime(), b.getTime());
  }

  /**
   *
   */
  @NotNull
  public static DateIntervals calculateIntervals(long a, long b) {
    return calculateIntervals(b - a);
  }

  /**
   *
   */
  @NotNull
  public static DateIntervals calculateIntervals(long intervalMillis) {
    int days = (int) (intervalMillis / MILLIS_PER_DAY);
    int hours = (int) ((intervalMillis - days * MILLIS_PER_DAY) / MILLIS_PER_HOUR);
    int minutes = (int) ((intervalMillis - days * MILLIS_PER_DAY - hours * MILLIS_PER_HOUR) / MILLIS_PER_MINUTE);
    int seconds = (int) ((intervalMillis - days * MILLIS_PER_DAY - hours * MILLIS_PER_HOUR - minutes * MILLIS_PER_MINUTE) / MILLIS_PER_SECOND);
    int milliseconds = (int) (intervalMillis % 1000);
    DateIntervals intervals = new DateIntervals();
    intervals.setDays(days);
    intervals.setHours(hours);
    intervals.setMinutes(minutes);
    intervals.setSeconds(seconds);
    intervals.setMilliseconds(milliseconds);
    return intervals;
  }
}
