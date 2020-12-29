package com.openjdl.jsf.webflux.socket;

import com.openjdl.jsf.webflux.socket.payload.SocketPayload;
import com.openjdl.jsf.webflux.socket.payload.SocketPayloadHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  protected void decode(@NotNull ChannelHandlerContext ctx, @NotNull ByteBuf in, @NotNull List<Object> out) throws Exception {
    // log
    if (log.isTraceEnabled()) {
      log.trace("Received bytes {}", ByteBufUtil.hexDump(in).toUpperCase());
    }

    // 保证有足够的长度
    if (in.readableBytes() < 14) {
      log.trace("Data not enough for decode: {} < {}", in.readableBytes(), 14);
      return;
    }

    // 标记
    in.markReaderIndex();

    // 检查掩码
    short mask = in.readUnsignedByte();
    if (mask != 0xFF) {
      in.resetReaderIndex();
      ctx.channel().close();
      return;
    }

    // 读取基本信息
    short version = in.readUnsignedByte();
    long id = in.readUnsignedInt();
    long type = in.readUnsignedInt();
    long length = in.readUnsignedInt();

    // 检查包体长度
    if (in.readableBytes() < length) {
      log.trace("Data not enough for decode: {} < {}", in.readableBytes(), length);
      in.resetReaderIndex();
      return;
    }

    byte[] body = new byte[(int) length];
    in.readBytes(body);

    // 消息头
    SocketPayloadHeader header = new SocketPayloadHeader(mask, version, id, type, length);

    // 载荷
    SocketPayload payload = new SocketPayload(header, body);

    // ok
    out.add(payload);
  }
}
