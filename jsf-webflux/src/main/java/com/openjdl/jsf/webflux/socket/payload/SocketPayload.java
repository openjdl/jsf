package com.openjdl.jsf.webflux.socket.payload;

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

  private final SocketPayloadHeader header;
  private SocketPayloadBodyExternalizable body;

  public SocketPayload(SocketPayloadHeader header, SocketPayloadBodyExternalizable body) {
    this.header = header;
    this.body = body;
  }

  public SocketPayloadHeader getHeader() {
    return header;
  }

  public SocketPayloadBodyExternalizable getBody() {
    return body;
  }

  public void setBody(SocketPayloadBodyExternalizable body) {
    this.body = body;
  }
}
