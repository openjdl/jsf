package com.openjdl.jsf.webflux.modbus.dtu.payload.request;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-07 22:50:53
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuExceptionRequest implements ModbusDtuRequest {
  /**
   * 功能码
   */
  private final short fc;

  /**
   * 错误码
   */
  private final short code;

  /**
   *
   */
  public ModbusDtuExceptionRequest(short fc, short code) {
    this.fc = fc;
    this.code = code;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuExceptionRequest{" +
      "fc=" + getFc() +
      ", byteCount=" + getByteCount() +
      ", code=" + getCode() +
      '}';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getFc() {
    return fc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getByteCount() {
    return 2;
  }

  /**
   * {@inheritDoc}
   */
  public short getCode() {
    return code;
  }

  @Override
  public void write(@NotNull ByteBuf out) {
    out.writeShort(fc);
    out.writeShort(code);
  }
}
