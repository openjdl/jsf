package com.openjdl.jsf.webflux.modbus.dtu.payload.response;

import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuFc;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-15 11:03:52
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuWriteMultipleCoilsResponse implements ModbusDtuResponse {
  /**
   * log
   */
  protected static final Logger log = LoggerFactory.getLogger(ModbusDtuWriteMultipleCoilsResponse.class);

  /**
   * 创建
   */
  @Nullable
  public static ModbusDtuWriteMultipleCoilsResponse of(@NotNull ByteBuf in) {
    if (in.readableBytes() < 4) {
      log.trace("Data not enough: {} < {}", in.readableBytes(), 4);
      return null;
    }

    int start = in.readUnsignedShort();
    int count = in.readUnsignedShort();

    return new ModbusDtuWriteMultipleCoilsResponse(start, count);
  }

  /**
   * 起始
   */
  private final int start;

  /**
   * 数据值
   */
  private final int count;

  /**
   *
   */
  public ModbusDtuWriteMultipleCoilsResponse(int start, int count) {
    this.start = start;
    this.count = count;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuWriteMultipleCoilsResponse{" +
      "fc=" + getFc() +
      ", start=" + getStart() +
      ", count=" + getCount() +
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
    return 5;
  }

  /**
   *
   */
  public int getStart() {
    return start;
  }

  /**
   *
   */
  public int getCount() {
    return count;
  }
}

