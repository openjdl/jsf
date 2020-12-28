package com.openjdl.jsf.webflux.socket.payload;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-25 15:04:30
 *
 * @author zink
 * @since 2.0.0
 */
public interface SocketPayloadBody {
  /**
   * 反序列化
   */
  default void deserialize(@NotNull byte[] data) {}

  /**
   * 序列化
   */
  default void serialize(@NotNull ByteBuf out) {}
}
