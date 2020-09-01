package io.tdi.jsf.core.exception;

import org.jetbrains.annotations.NotNull;

/**
 * 已经格式化了的异常
 * <p>
 * Created at 2020-08-04 22:44:29
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsfResolvedException extends JsfException {
  /**
   *
   */
  public JsfResolvedException(@NotNull JsfExceptionData data) {
    super(data);
  }

  /**
   *
   */
  public JsfResolvedException(@NotNull String message, @NotNull JsfExceptionData data) {
    super(message, data);
  }

  /**
   *
   */
  public JsfResolvedException(@NotNull String message, @NotNull Throwable cause, @NotNull JsfExceptionData data) {
    super(message, cause, data);
  }

  /**
   *
   */
  public JsfResolvedException(@NotNull Throwable cause, @NotNull JsfExceptionData data) {
    super(cause, data);
  }
}
