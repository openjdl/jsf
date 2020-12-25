package com.openjdl.jsf.webflux.socket;

import com.openjdl.jsf.webflux.socket.payload.SocketPayload;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadBodyExternalizable;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-23 18:25:37
 *
 * @author zink
 * @since 0.0.1
 */
public class SocketEncoder extends MessageToByteEncoder<SocketPayload> {
  /**
   * log
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  protected void encode(ChannelHandlerContext ctx, SocketPayload payload, ByteBuf out) throws Exception {
    SocketPayloadHeader header = payload.getHeader();
    SocketPayloadBodyExternalizable body = payload.getBody();

    out.writeByte(header.getMask());
    out.writeByte(header.getVersion());
    out.writeInt((int) header.getId());
    out.writeInt((int) header.getType());

    if (body != null) {
      ByteBuf bodyOut = Unpooled.buffer();
      body.serialize(bodyOut);
      out.writeInt(bodyOut.writerIndex());
      out.writeBytes(bodyOut);
    } else {
      out.writeInt(0);
    }
  }
}
