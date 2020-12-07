package com.openjdl.jsf.webflux.modbus.dtu;

import com.openjdl.jsf.webflux.modbus.dtu.payload.ModbusDtuPayload;
import com.openjdl.jsf.webflux.modbus.dtu.payload.response.ModbusDtuRegisterResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created at 2020-12-07 20:39:42
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuDecoder extends ByteToMessageDecoder {
  /**
   * log
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * {@inheritDoc}
   */
  @Override
  protected void decode(@NotNull ChannelHandlerContext ctx, @NotNull ByteBuf in, @NotNull List<Object> out) {
    // 保证有足够的长度
    if (in.readableBytes() < Integer.BYTES) {
      log.trace("数据长度不够: {} < {}", in.readableBytes(), Integer.BYTES);
      return;
    }

    // 标记
    in.markReaderIndex();

    // 协议
    long protocol = in.readUnsignedInt();

    // 处理
    boolean successful;

    try {
      if (protocol == 0x0000FFFF) {
        successful = decodeRegisterMessage(ctx, in, out);
      } else if (protocol == 0x0000FFFE) {
        successful = decodeHeartbeatMessage(ctx, in, out);
      } else {
        successful = decodeDataMessage(ctx, in, out);
      }
    } catch (Exception e) {
      log.error("", e);
      successful = false;
    }

    if (!successful) {
      in.resetReaderIndex();
    }
  }

  /**
   * 解码注册消息
   */
  private boolean decodeRegisterMessage(@NotNull ChannelHandlerContext ctx, @NotNull ByteBuf in, @NotNull List<Object> out) {
    // 载荷长度
    int length = in.readUnsignedShort();

    // 保证有足够的长度
    if (in.readableBytes() < length - 6) {
      log.trace("注册数据长度不够: {} < {}", in.readableBytes(), length - 6);
      return false;
    }

    // 读取数据
    String data = in
      .readCharSequence(length, StandardCharsets.UTF_8)
      .toString();

    // done
    out.add(ModbusDtuPayload.of(ModbusDtuPayload.Type.REGISTER, new ModbusDtuRegisterResponse(data)));

    // ok
    return true;
  }

  /**
   * 解码心跳消息
   */
  private boolean decodeHeartbeatMessage(@NotNull ChannelHandlerContext ctx, @NotNull ByteBuf in, @NotNull List<Object> out) {
    // done
    out.add(ModbusDtuPayload.of(ModbusDtuPayload.Type.HEARTBEAT));

    // ok
    return true;
  }

  /**
   * 结束数据消息
   */
  private boolean decodeDataMessage(@NotNull ChannelHandlerContext ctx, @NotNull ByteBuf in, @NotNull List<Object> out) {
    // 读取数据
    ModbusDtuPayload payload = ModbusDtuPayload.of(in);

    if (payload == null) {
      return false;
    }

    // done
    out.add(payload);

    // ok
    return true;
  }
}
