package io.tdi.jsf.settings.exception;

/**
 * Created at 2020-09-10 14:40:00
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsValidationException extends RuntimeException {
  public SettingsValidationException() {
  }

  public SettingsValidationException(String message) {
    super(message);
  }

  public SettingsValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public SettingsValidationException(Throwable cause) {
    super(cause);
  }
}
