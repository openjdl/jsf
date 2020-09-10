package io.tdi.jsf.settings.exception;

/**
 * Created at 2020-09-10 14:39:34
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsException extends RuntimeException {
  public SettingsException() {
  }

  public SettingsException(String message) {
    super(message);
  }

  public SettingsException(String message, Throwable cause) {
    super(message, cause);
  }

  public SettingsException(Throwable cause) {
    super(cause);
  }
}
