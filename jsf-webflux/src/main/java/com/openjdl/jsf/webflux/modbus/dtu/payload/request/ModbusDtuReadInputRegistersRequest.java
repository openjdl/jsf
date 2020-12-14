package com.openjdl.jsf.webflux.modbus.dtu.payload.request;

import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuFc;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-14 11:39:55
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuReadInputRegistersRequest implements ModbusDtuRequest {
  /**
   * 起始地址
   */
  private final int start;

  /**
   * 数据个数
   */
  private final int count;

  /**
   *
   */
  public ModbusDtuReadInputRegistersRequest(int start, int count) {
    if (start < 0 || start > 0xFFFF) { // [0, 65535]
      throw new IllegalArgumentException("Invalid start address: " + start);
    }
    if (count < 1 || count > 0x07D0) { // [1, 2000]
      throw new IllegalArgumentException("Invalid count: " + start);
    }
    this.start = start;
    this.count = count;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuReadInputRegistersRequest{" +
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
    return ModbusDtuFc.READ_INPUT_REGISTERS.getCode();
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
  public int getStart() {
    return start;
  }

  /**
   * 数据个数
   */
  public int getCount() {
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
