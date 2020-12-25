package com.openjdl.jsf.webflux.socket;

import com.openjdl.jsf.webflux.socket.payload.SocketPayload;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadBodyExternalizable;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.util.List;

/**
 * Created at 2020-12-23 18:23:52
 *
 * @author zink
 * @since 0.0.1
 */
public class SocketDecoder extends ByteToMessageDecoder {
  /**
   * log
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final SocketSessionManager sessionManager;

  public SocketDecoder(SocketSessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  @Override
  protected void decode(@NotNull ChannelHandlerContext ctx, @NotNull ByteBuf in, @NotNull List<Object> out) {
    // log
    if (log.isTraceEnabled()) {
      log.trace("Received bytes {}", ByteBufUtil.hexDump(in).toUpperCase());
    }

    // 保证有足够的长度
    if (in.readableBytes() < 14) {
      log.trace("Data not enough for decode: {} < {}", in.readableBytes(), 14);
      return;
    }

    in.markReaderIndex();

    short mask = in.readUnsignedByte();
    short version = in.readUnsignedByte();
    long id = in.readUnsignedInt();
    long type = in.readUnsignedInt();
    long length = in.readUnsignedInt();

    log.trace("mask={}, version{}, id={}, type={}, length={}", mask, version, id, type, length);

    if (in.readableBytes() < length) {
      log.trace("Data not enough for decode: {} < {}", in.readableBytes(), length);
      return;
    }

    //
    SocketPayloadHeader header = new SocketPayloadHeader(mask, version, id, type, length);
    SocketPayloadBodyExternalizable body = sessionManager.createPayloadBody(type, in);

    out.add(new SocketPayload(header, body));
  }
}
