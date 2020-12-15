package com.openjdl.jsf.webflux.modbus.dtu.payload.request;

import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuFc;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created at 2020-12-15 11:36:56
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuWriteMultipleHoldingRegistersRequest implements ModbusDtuRequest {
  /**
   * 地址
   */
  private final int address;

  /**
   * 值
   */
  private final int[] values;

  /**
   *
   */
  public ModbusDtuWriteMultipleHoldingRegistersRequest(int address, int... values) {
    if (address < 0 || address > 0xFFFF) { // [0, 65535]
      throw new IllegalArgumentException("Invalid address: " + address);
    }
    if (values.length > 0x07B) {
      throw new IllegalArgumentException("Invalid values length: " + values.length);
    }
    for (int value : values) {
      if (value < 0 || value > 0xFFFF) {
        throw new IllegalArgumentException("Invalid values: " + values.length);
      }
    }
    this.address = address;
    this.values = values;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuWriteMultipleHoldingRegistersRequest{" +
      "fc=" + getFc() +
      ", byteCount=" + getByteCount() +
      ", address=" + getAddress() +
      ", values=" + Arrays.toString(getValues()) +
      '}';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getFc() {
    return ModbusDtuFc.WRITE_MULTIPLE_HOLDING_REGISTERS.getCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getByteCount() {
    return (short) (values.length * 2 + 6);
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
  public int[] getValues() {
    return values;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NotNull ByteBuf out) {
    out.writeByte(getFc()); // 0
    out.writeShort(getAddress()); // 1-2
    out.writeShort(getValues().length); // 3-4
    out.writeByte(getValues().length * 2);

    for (int value : values) {
      out.writeShort(value);
    }
  }
}