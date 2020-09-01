package io.tdi.jsf.core.utils;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class DateIntervals {
  private int days;
  private int hours;
  private int minutes;
  private int seconds;
  private int milliseconds;

  /**
   *
   */
  public DateIntervals() {
  }

  /**
   *
   */
  public DateIntervals(int days, int hours, int minutes, int seconds, int milliseconds) {
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.milliseconds = milliseconds;
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }

  public int getHours() {
    return hours;
  }

  public void setHours(int hours) {
    this.hours = hours;
  }

  public int getMinutes() {
    return minutes;
  }

  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  public int getSeconds() {
    return seconds;
  }

  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }

  public int getMilliseconds() {
    return milliseconds;
  }

  public void setMilliseconds(int milliseconds) {
    this.milliseconds = milliseconds;
  }
}
