package com.openjdl.jsf.webflux.modbus.dtu.payload.response;

import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuFc;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-14 16:36:31
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuWriteSingleCoilResponse implements ModbusDtuResponse {
  /**
   * log
   */
  protected static final Logger log = LoggerFactory.getLogger(ModbusDtuWriteSingleCoilResponse.class);

  /**
   * 创建
   */
  @Nullable
  public static ModbusDtuWriteSingleCoilResponse of(@NotNull ByteBuf in) {
    if (in.readableBytes() < 4) {
      log.trace("Data not enough: {} < {}", in.readableBytes(), 4);
      return null;
    }

    int addr = in.readUnsignedShort();
    boolean flag = in.readUnsignedShort() == 0xFF00;

    return new ModbusDtuWriteSingleCoilResponse(addr, flag);
  }

  /**
   * 地址
   */
  private final int addr;

  /**
   * 数据值
   */
  private final boolean flag;

  /**
   *
   */
  public ModbusDtuWriteSingleCoilResponse(int addr, boolean flag) {
    this.addr = addr;
    this.flag = flag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuWriteSingleCoilResponse{" +
      "fc=" + getFc() +
      "addr=" + getAddr() +
      ", flag=" + isFlag() +
      '}';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getFc() {
    return ModbusDtuFc.WRITE_SINGLE_COIL.getCode();
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
  public boolean isFlag() {
    return flag;
  }
}

