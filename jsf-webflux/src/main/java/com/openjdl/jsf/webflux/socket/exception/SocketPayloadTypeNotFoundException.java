package com.openjdl.jsf.webflux.socket.exception;

/**
 * Created at 2020-12-27 12:49:17
 *
 * @author kidal
 * @since 0.5
 */
public class SocketPayloadTypeNotFoundException extends Exception {
  private static final long serialVersionUID = 3103754887644104808L;

  public SocketPayloadTypeNotFoundException() {
  }

  public SocketPayloadTypeNotFoundException(String message) {
    super(message);
  }

  public SocketPayloadTypeNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public SocketPayloadTypeNotFoundException(Throwable cause) {
    super(cause);
  }
}
