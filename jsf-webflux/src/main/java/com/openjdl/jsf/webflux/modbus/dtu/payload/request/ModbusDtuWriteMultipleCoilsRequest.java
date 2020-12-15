package com.openjdl.jsf.webflux.modbus.dtu.payload.request;

import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuFc;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created at 2020-12-15 10:28:32
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuWriteMultipleCoilsRequest implements ModbusDtuRequest {
  /**
   * 地址
   */
  private final int address;

  /**
   * 值
   */
  private final boolean[] values;

  /**
   * 字节数
   */
  private final short byteCount;

  /**
   *
   */
  public ModbusDtuWriteMultipleCoilsRequest(int address, boolean[] values) {
    if (address < 0 || address > 0xFFFF) { // [0, 65535]
      throw new IllegalArgumentException("Invalid address: " + address);
    }
    if (values.length > 0x07B0 * 8) {
      throw new IllegalArgumentException("Invalid values length: " + values.length);
    }
    this.address = address;
    this.values = values;
    this.byteCount = (short) (values.length / 8 + 6);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuWriteMultipleCoilsRequest{" +
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
    return ModbusDtuFc.WRITE_MULTIPLE_COILS.getCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getByteCount() {
    return byteCount;
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
  public boolean[] getValues() {
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
    out.writeByte(getValues().length / 8);

    for (boolean value : values) {
      out.writeByte(value ? 1 : 0);
    }
  }
}