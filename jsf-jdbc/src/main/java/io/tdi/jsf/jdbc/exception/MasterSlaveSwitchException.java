package io.tdi.jsf.jdbc.exception;

/**
 * Created at 2020-08-06 17:23:30
 *
 * @author kidal
 * @since 0.1.0
 */
public class MasterSlaveSwitchException extends RuntimeException {
  /**
   *
   */
  public MasterSlaveSwitchException() {
  }

  /**
   *
   */
  public MasterSlaveSwitchException(String message) {
    super(message);
  }

  /**
   *
   */
  public MasterSlaveSwitchException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   *
   */
  public MasterSlaveSwitchException(Throwable cause) {
    super(cause);
  }
}
