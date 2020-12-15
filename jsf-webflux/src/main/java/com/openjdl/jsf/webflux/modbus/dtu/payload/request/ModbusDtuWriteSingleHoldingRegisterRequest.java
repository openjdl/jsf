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
public class ModbusDtuWriteSingleHoldingRegisterRequest implements ModbusDtuRequest {
  /**
   * 地址
   */
  private final int address;

  /**
   * 值
   */
  private final int value;

  /**
   *
   */
  public ModbusDtuWriteSingleHoldingRegisterRequest(int address, int value) {
    if (address < 0 || address > 0xFFFF) { // [0, 65535]
      throw new IllegalArgumentException("Invalid addr: " + address);
    }
    if (value < 0 || value > 0xFFFF) { // [0, 65535]
      throw new IllegalArgumentException("Invalid value: " + value);
    }
    this.address = address;
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
      ", address=" + getAddress() +
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
  public int getAddress() {
    return address;
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
    out.writeShort(getAddress()); // 1-2
    out.writeShort(getValue()); // 3-4
  }
}
