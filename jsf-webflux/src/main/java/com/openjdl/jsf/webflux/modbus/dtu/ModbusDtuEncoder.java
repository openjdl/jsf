package com.openjdl.jsf.webflux.modbus.dtu;

import com.openjdl.jsf.webflux.modbus.dtu.payload.ModbusDtuPayload;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-12-07 22:32:34
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuEncoder extends MessageToByteEncoder<ModbusDtuPayload> {
  @Override
  protected void encode(@NotNull ChannelHandlerContext ctx, @NotNull ModbusDtuPayload payload, @NotNull ByteBuf out) {
    payload.getRequest().write(out);
  }
}
