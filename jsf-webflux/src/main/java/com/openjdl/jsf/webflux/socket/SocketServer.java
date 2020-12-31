package com.openjdl.jsf.webflux.socket;

import com.openjdl.jsf.webflux.boot.JsfWebFluxProperties;
import com.openjdl.jsf.webflux.socket.payload.SocketPayload;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created at 2020-12-23 16:27:06
 *
 * @author zink
 * @since 0.0.1
 */
public class SocketServer {
  /**
   * 日志
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * 属性
   */
  @NotNull
  private final JsfWebFluxProperties.SocketServer properties;

  /**
   * 会话管理
   */
  @NotNull
  private final SocketSessionManager sessionManager;

  /**
   * 主循环
   */
  private NioEventLoopGroup bossGroup;

  /**
   * 工作循环
   */
  private NioEventLoopGroup workerGroup = new NioEventLoopGroup();

  /**
   *
   */
  private ChannelFuture bindChannelFuture;

  /**
   *
   */
  public SocketServer(@NotNull JsfWebFluxProperties.SocketServer properties,
                      @NotNull SocketSessionManager sessionManager) {
    this.properties = properties;
    this.sessionManager = sessionManager;
  }

  /**
   * 启动 Netty 服务器
   */
  public void initNettyServer() throws InterruptedException {
    bossGroup = new NioEventLoopGroup(properties.getBossThreads());
    workerGroup = new NioEventLoopGroup(properties.getWorkerThreads());

    ServerBootstrap bootstrap = new ServerBootstrap();

    bootstrap.group(bossGroup, workerGroup)
      .channel(NioServerSocketChannel.class)
      .option(ChannelOption.SO_BACKLOG, properties.getBacklog()) // 链接缓冲池的大小
      .childOption(ChannelOption.SO_KEEPALIVE, true) // 维持链接的活跃，清除死链接
      .childOption(ChannelOption.TCP_NODELAY, true) // 关闭延迟发送
      .childHandler(new ChannelInitializer<SocketChannel>() {

        @Override
        protected void initChannel(@NotNull SocketChannel ch) {
          if (log.isTraceEnabled()) {
            log.trace("Channel({}) init", ch);
          }

          ch
            .pipeline()
            .addLast("encoder", new SocketEncoder())
            .addLast("decoder", new SocketDecoder(sessionManager))
            .addLast("handler", new Handler());
        }
      });

    // 绑定
    bindChannelFuture = bootstrap.bind(properties.getPort()).sync();

    if (bindChannelFuture.isSuccess()) {
      log.info("Socket server started on port: {}", properties.getPort());
    } else {
      throw new IllegalStateException("Socket server started failed");
    }
  }

  /**
   * 关闭 Netty 服务器
   */
  public void shutNettyServer() {
    // 等待关闭信号
    if (bindChannelFuture != null) {
      bindChannelFuture.channel().closeFuture().addListener((ChannelFutureListener) future -> {
        log.trace("Socket server close on port: {}", properties.getPort());
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
      });
      bindChannelFuture.channel().close();
      bindChannelFuture = null;
    }
  }

  //--------------------------------------------------------------------------
  // ChannelInboundHandlerAdapter
  //--------------------------------------------------------------------------
  //region

  /**
   *
   */
  class Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
      if (log.isTraceEnabled()) {
        log.trace("Channel({}) read: {}", ctx.channel(), msg.toString());
      }

      SocketPayload payload = (SocketPayload) msg;
      long type = payload.getHeader().getType();

      // 获取会话
      SocketSession session = (SocketSession) ctx.channel().attr(AttributeKey.valueOf("session")).get();

      // 处理 rpc
      session.onResponse(payload);

      // 处理消息
      SocketResponseHandler handler = sessionManager.getRequestHandler(type);

      if (handler == null) {
        log.warn("Channel({}) handle failed, no handler for type `{}`", ctx.channel(), type);
      } else {
        handler.handle(session, payload);
      }

      // done
      super.channelRead(ctx, msg);
    }

    /**
     * 捕获异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      log.error("Channel({}) exception caught", ctx.channel(), cause);

      // 获取会话
      SocketSession session = (SocketSession) ctx.channel().attr(AttributeKey.valueOf("session")).get();

      // 关闭
      session.close();

      super.exceptionCaught(ctx, cause);
    }

    /**
     * 开启通道
     */
    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
      Channel channel = ctx.channel();

      if (log.isTraceEnabled()) {
        log.trace("Channel({}) active", channel);
      }

      // 创建会话
      SocketSession session = new SocketSession(sessionManager, channel);

      // 绑定会话
      channel.attr(AttributeKey.valueOf("session")).set(session);

      // 通知管理器
      sessionManager.onConnect(session);

      super.channelActive(ctx);
    }

    /**
     * 关闭通道
     */
    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
      if (log.isTraceEnabled()) {
        log.trace("Channel({}) inactive", ctx.channel());
      }

      // 获取会话
      SocketSession session = (SocketSession) ctx.channel().attr(AttributeKey.valueOf("session")).get();

      //关闭
      session.close();

      super.channelInactive(ctx);
    }
  }

  //endregion
}
