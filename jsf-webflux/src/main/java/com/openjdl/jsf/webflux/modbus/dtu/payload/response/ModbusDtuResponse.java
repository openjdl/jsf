package com.openjdl.jsf.webflux.modbus.dtu.payload.response;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-07 21:05:13
 *
 * @author kidal
 * @since 0.5
 */
public interface ModbusDtuResponse {
  /**
   * 空消息体
   */
  ModbusDtuEmptyResponse EMPTY = new ModbusDtuEmptyResponse();

  /**
   * 空消息体
   */
  class ModbusDtuEmptyResponse implements ModbusDtuResponse {
    @Override
    public String toString() {
      return "ModbusDtuEmptyResponse{}";
    }
  }

  /**
   * 功能码
   */
  default short getFc() {
    return 0;
  }

  /**
   * 字节数
   */
  default short getByteCount() {
    return 0;
  }

  /**
   * 写入
   */
  default void write(@NotNull ByteBuf out) {

  }
}
