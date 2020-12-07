package com.openjdl.jsf.webflux.modbus.dtu.payload.response;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created at 2020-12-07 22:17:40
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuReadCoilsResponse implements ModbusDtuResponse {
  /**
   * log
   */
  protected static final Logger log = LoggerFactory.getLogger(ModbusDtuReadCoilsResponse.class);

  /**
   * 创建
   */
  @Nullable
  public static ModbusDtuReadCoilsResponse of(@NotNull ByteBuf in) {
    if (in.readableBytes() < 1) {
      log.trace("数据长度不够: {} < {}", in.readableBytes(), 1);
      return null;
    }

    short byteCount = in.readUnsignedByte();

    if (in.readableBytes() < byteCount + 2) {
      log.trace("数据长度不够: {} < {}", in.readableBytes(), byteCount + 2);
      return null;
    }

    byte[] values = new byte[byteCount + 2];
    in.readBytes(values);

    return new ModbusDtuReadCoilsResponse(byteCount, values);
  }

  /**
   * 字节数
   */
  private final short byteCount;

  /**
   * 数据值
   */
  private final byte[] values;

  /**
   *
   */
  public ModbusDtuReadCoilsResponse(short byteCount, byte[] values) {
    this.byteCount = byteCount;
    this.values = values;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuReadCoilsResponse{" +
      "fc=" + getFc() +
      "byteCount=" + getByteCount() +
      ", values=" + Arrays.toString(getValues()) +
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
    return byteCount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public byte[] getValues() {
    return values;
  }
}
