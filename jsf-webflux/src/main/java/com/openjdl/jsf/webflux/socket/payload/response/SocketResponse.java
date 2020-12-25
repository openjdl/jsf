package com.openjdl.jsf.webflux.socket.payload.response;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-23 17:26:25
 *
 * @author zink
 * @since 2.0.0
 */
public interface SocketResponse {
  /**
   * 空消息体
   */
  SocketEmptyResponse EMPTY = new SocketEmptyResponse();

  /**
   * 空消息体类
   */
  class SocketEmptyResponse implements SocketResponse {
    @Override
    public String toString() {
      return "SocketEmptyResponse{}";
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
