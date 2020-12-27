package com.openjdl.jsf.webflux.socket.payload;

/**
 * Created at 2020-12-25 15:01:54
 *
 * @author zink
 * @since 2.0.0
 */
public class SocketPayloadHeader {
  private final short mask;
  private final short version;
  private final long id;
  private final long type;
  private final long length;

  public SocketPayloadHeader(long id, long type) {
    this((short) 0xFF, (short) 0x01, id, type, 0);
  }

  public SocketPayloadHeader(short mask, short version, long id, long type, long length) {
    this.mask = mask;
    this.version = version;
    this.id = id;
    this.type = type;
    this.length = length;
  }

  public short getMask() {
    return mask;
  }

  public short getVersion() {
    return version;
  }

  public long getId() {
    return id;
  }

  public long getType() {
    return type;
  }

  public long getLength() {
    return length;
  }
}
