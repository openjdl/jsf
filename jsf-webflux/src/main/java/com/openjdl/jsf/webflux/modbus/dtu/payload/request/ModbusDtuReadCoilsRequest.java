package com.openjdl.jsf.webflux.modbus.dtu.payload.request;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-07 22:55:27
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuReadCoilsRequest implements ModbusDtuRequest {
  /**
   * 起始地址
   */
  private final short start;

  /**
   * 数据个数
   */
  private final short count;

  /**
   *
   */
  public ModbusDtuReadCoilsRequest(short start, short count) {
    this.start = start;
    this.count = count;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuReadCoilsRequest{" +
      "fc=" + getFc() +
      ", byteCount=" + getByteCount() +
      ", start=" + getStart() +
      ", count=" + getCount() +
      '}';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getFc() {
    return 0x01;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getByteCount() {
    return 5;
  }

  /**
   * 起始地址
   */
  public short getStart() {
    return start;
  }

  /**
   * 数据个数
   */
  public short getCount() {
    return count;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NotNull ByteBuf out) {
    out.writeByte(getFc());
    out.writeShort(getStart());
    out.writeShort(getCount());
  }
}
