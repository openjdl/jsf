package com.openjdl.jsf.webflux.modbus.dtu;

import com.openjdl.jsf.webflux.modbus.dtu.payload.ModbusDtuPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-07 22:32:34
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuEncoder extends MessageToByteEncoder<ModbusDtuPayload> {
  /**
   * log
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   *
   */
  @Override
  protected void encode(@NotNull ChannelHandlerContext ctx, @NotNull ModbusDtuPayload payload, @NotNull ByteBuf out) {
    if (log.isTraceEnabled()) {
      log.trace("Before encode {} to {}", payload, ByteBufUtil.hexDump(out));
    }

    payload.write(out);

    if (log.isTraceEnabled()) {
      log.trace("After encode {} to {}", payload, ByteBufUtil.hexDump(out));
    }
  }
}
