package com.openjdl.jsf.webflux.modbus.dtu.payload.response;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-07 21:11:51
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuExceptionResponse implements ModbusDtuResponse {
  /**
   * log
   */
  protected static final Logger log = LoggerFactory.getLogger(ModbusDtuExceptionResponse.class);

  /**
   * 创建
   */
  @Nullable
  public static ModbusDtuExceptionResponse of(short fc, @NotNull ByteBuf in) {
    if (in.readableBytes() < 1) {
      log.trace("数据长度不够: {} < {}", in.readableBytes(), 1);
      return null;
    }

    short code = in.readUnsignedByte();

    return new ModbusDtuExceptionResponse(fc, code);
  }

  /**
   * 功能码
   */
  private final short fc;

  /**
   * 错误码
   */
  private final short code;

  /**
   *
   */
  public ModbusDtuExceptionResponse(short fc, short code) {
    this.fc = fc;
    this.code = code;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ModbusDtuExceptionResponse{" +
      "fc=" + getFc() +
      ", byteCount=" + getByteCount() +
      ", code=" + getCode() +
      ", message=" + getMessage() +
      '}';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getFc() {
    return this.fc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getByteCount() {
    return 2;
  }

  /**
   *
   */
  public int getCode() {
    return code;
  }

  /**
   *
   */
  @NotNull
  public String getMessage() {
    switch (code) {
      case 0x01:
        return "ILLEGAL FUNCTION";
      case 0x02:
        return "ILLEGAL DATA ADDRESS";
      case 0x03:
        return "ILLEGAL DATA VALUE";
      case 0x04:
        return "SLAVE DEVICE FAILURE";
      case 0x05:
        return "ACKNOWLEDGE";
      case 0x06:
        return "SLAVE DEVICE BUSY";
      case 0x08:
        return "MEMORY PARITY ERROR";
      case 0x0a:
        return "GATEWAY PATH UNAVAILABLE";
      case 0x0b:
        return "GATEWAY TARGET DEVICE FAILED TO RESPOND";
      default:
        return "UNKNOWN(" + code + ")";
    }
  }
}
