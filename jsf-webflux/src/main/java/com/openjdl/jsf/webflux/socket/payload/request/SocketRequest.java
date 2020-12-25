package com.openjdl.jsf.webflux.socket.payload.request;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-24 10:47:29
 *
 * @author zink
 * @since 0.0.1
 */
public interface SocketRequest {
  /**
   * 空消息体
   */
  SocketRequest EMPTY = new SocketEmptyRequest();

  /**
   * 空消息体
   */
  class SocketEmptyRequest implements SocketRequest {
    @Override
    public String toString() {
      return "SocketEmptyRequest{}";
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
