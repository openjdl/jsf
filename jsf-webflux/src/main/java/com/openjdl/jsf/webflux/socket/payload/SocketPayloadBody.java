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
  void deserialize(@NotNull ByteBuf in);

  /**
   * 序列化
   */
  void serialize(@NotNull ByteBuf out);
}
