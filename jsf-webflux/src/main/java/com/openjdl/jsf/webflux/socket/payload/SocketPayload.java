package com.openjdl.jsf.webflux.socket.payload;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-23 17:37:39
 *
 * @author zink
 * @since 0.0.1
 */
public class SocketPayload {
  /**
   * log
   */
  private static final Logger log = LoggerFactory.getLogger(SocketPayload.class);

  @NotNull
  private final SocketPayloadHeader header;

  @NotNull
  private final Object body;

  /**
   * Ctor.
   */
  public SocketPayload(@NotNull SocketPayloadHeader header, @NotNull Object body) {
    this.header = header;
    this.body = body;
  }

  //--------------------------------------------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------------------------------------------
  //region

  @NotNull
  public SocketPayloadHeader getHeader() {
    return header;
  }

  @NotNull
  public Object getBody() {
    return body;
  }

  //endregion
}
