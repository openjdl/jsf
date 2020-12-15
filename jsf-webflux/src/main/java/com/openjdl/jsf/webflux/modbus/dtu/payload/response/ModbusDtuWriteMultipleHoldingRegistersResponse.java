package com.openjdl.jsf.webflux.modbus.dtu.payload.response;

import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuFc;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-15 11:42:04
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuWriteMultipleHoldingRegistersResponse implements ModbusDtuResponse {
  /**
   * log
   */
  protected static final Logger log = LoggerFactory.getLogger(ModbusDtuWriteMultipleHoldingRegistersResponse.class);

  /**
   * 创建
   */
  @Nullable
  public static ModbusDtuWriteMultipleHoldingRegistersResponse of(@NotNull ByteBuf in) {
    if (in.readableBytes() < 4) {
      log.trace("Data not enough: {} < {}", in.readableBytes(), 4);
      return null;
    }

    int address = in.readUnsignedShort();
    int count = in.readUnsignedShort();

    return new ModbusDtuWriteMultipleHoldingRegistersResponse(address, count);
  }

  /**
   * 地址
   */
  private final int address;

  /**
   * 数据值
   */
  private final int count;

  /**
   *
   */
  public ModbusDtuWriteMultipleHoldingRegistersResponse(int address, int count) {
    this.address = address;
    this.count = count;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuWriteSingleHoldingRegisterResponse{" +
      "fc=" + getFc() +
      ", address=" + getAddress() +
      ", count=" + getCount() +
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
    return 5;
  }

  /**
   * 获取地址
   */
  public int getAddress() {
    return address;
  }

  /**
   * 获取值
   */
  public int getCount() {
    return count;
  }
}


