package com.openjdl.jsf.webflux.modbus.dtu.exception;

/**
 * Created at 2020-12-08 15:48:26
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuSendTimeoutException extends Exception {
  private static final long serialVersionUID = 8468865042702686955L;

  public ModbusDtuSendTimeoutException() {
    super();
  }

  public ModbusDtuSendTimeoutException(String message) {
    super(message);
  }

  public ModbusDtuSendTimeoutException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModbusDtuSendTimeoutException(Throwable cause) {
    super(cause);
  }
}
