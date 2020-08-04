package org.kidal.jsf.core.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-04 18:41:32
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsfException extends RuntimeException {
  /**
   * 错误数据
   */
  private final JsfExceptionData data;

  /**
   * 错误信息格式化参数
   */
  private final Object[] formatArguments;

  /**
   *
   */
  public JsfException(@NotNull JsfExceptionData data, Object... formatArguments) {
    this.data = data;
    this.formatArguments = formatArguments;
  }

  /**
   *
   */
  public JsfException(@NotNull String message, @NotNull JsfExceptionData data, Object... formatArguments) {
    super(message);
    this.data = data;
    this.formatArguments = formatArguments;
  }

  /**
   *
   */
  public JsfException(@NotNull String message, @NotNull Throwable cause, @NotNull JsfExceptionData data, Object... formatArguments) {
    super(message, cause);
    this.data = data;
    this.formatArguments = formatArguments;
  }

  /**
   *
   */
  public JsfException(@NotNull Throwable cause, @NotNull JsfExceptionData data, Object... formatArguments) {
    super(cause);
    this.data = data;
    this.formatArguments = formatArguments;
  }

  /**
   *
   */
  @NotNull
  public JsfExceptionData getData() {
    return data;
  }

  /**
   *
   */
  @NotNull
  public Object[] getFormatArguments() {
    return formatArguments;
  }
}