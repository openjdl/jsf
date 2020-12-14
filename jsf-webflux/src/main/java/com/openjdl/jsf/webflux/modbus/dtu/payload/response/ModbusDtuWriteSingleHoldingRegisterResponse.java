package com.openjdl.jsf.webflux.modbus.dtu.payload.response;

import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuFc;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-14 16:39:55
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuWriteSingleHoldingRegisterResponse implements ModbusDtuResponse {
  /**
   * log
   */
  protected static final Logger log = LoggerFactory.getLogger(ModbusDtuWriteSingleHoldingRegisterResponse.class);

  /**
   * 创建
   */
  @Nullable
  public static ModbusDtuWriteSingleHoldingRegisterResponse of(@NotNull ByteBuf in) {
    if (in.readableBytes() < 4) {
      log.trace("Data not enough: {} < {}", in.readableBytes(), 4);
      return null;
    }

    int addr = in.readUnsignedShort();
    int value = in.readUnsignedShort();

    return new ModbusDtuWriteSingleHoldingRegisterResponse(addr, value);
  }

  /**
   * 地址
   */
  private final int addr;

  /**
   * 数据值
   */
  private final int value;

  /**
   *
   */
  public ModbusDtuWriteSingleHoldingRegisterResponse(int addr, int value) {
    this.addr = addr;
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuWriteSingleHoldingRegisterResponse{" +
      "fc=" + getFc() +
      "addr=" + getAddr() +
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
   * 获取地址
   */
  public int getAddr() {
    return addr;
  }

  /**
   * 获取值
   */
  public int getValue() {
    return value;
  }
}


