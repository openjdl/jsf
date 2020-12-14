package com.openjdl.jsf.webflux.modbus.dtu.payload.request;

import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuFc;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-14 15:53:11
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuWriteSingleRegisterRequest implements ModbusDtuRequest {
  /**
   * 地址
   */
  private final int addr;

  /**
   * 值
   */
  private final int value;

  /**
   *
   */
  public ModbusDtuWriteSingleRegisterRequest(int addr, int value) {
    if (addr < 0 || addr > 0xFFFF) { // [0, 65535]
      throw new IllegalArgumentException("Invalid addr: " + addr);
    }
    if (value < 0 || value > 0xFFFF) { // [0, 65535]
      throw new IllegalArgumentException("Invalid value: " + value);
    }
    this.addr = addr;
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuWriteSingleRegisterRequest{" +
      "fc=" + getFc() +
      ", byteCount=" + getByteCount() +
      ", addr=" + getAddr() +
      ", value=" + getValue() +
      '}';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getFc() {
    return ModbusDtuFc.WRITE_SINGLE_HOLDING_REGISTER.getCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getByteCount() {
    return 5;
  }

  /**
   * 地址
   */
  public int getAddr() {
    return addr;
  }

  /**
   * 值
   */
  public int getValue() {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NotNull ByteBuf out) {
    out.writeByte(getFc()); // 0
    out.writeShort(getAddr()); // 1-2
    out.writeShort(getValue()); // 3-4
  }
}
