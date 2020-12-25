package com.openjdl.jsf.webflux.socket.payload;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-25 15:04:30
 *
 * @author zink
 * @since 2.0.0
 */
public interface SocketPayloadBodyExternalizable {
  void deserialize(@NotNull ByteBuf in);
  void serialize(@NotNull ByteBuf out);
}
