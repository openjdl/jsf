package com.openjdl.jsf.webflux.modbus.dtu.payload.request;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-08 16:53:33
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuReadHoldingRegistersRequest implements ModbusDtuRequest {
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
  public ModbusDtuReadHoldingRegistersRequest(short start, short count) {
    this.start = start;
    this.count = count;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuReadHoldingRegistersRequest{" +
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
    return 0x03;
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
    out.writeByte(getFc()); // 0
    out.writeShort(getStart()); // 1-2
    out.writeShort(getCount()); // 3-4
  }
}
