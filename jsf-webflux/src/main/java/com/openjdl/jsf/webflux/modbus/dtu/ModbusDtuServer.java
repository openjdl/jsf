package com.openjdl.jsf.webflux.modbus.dtu;

import com.openjdl.jsf.webflux.boot.JsfWebFluxProperties;
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
 * Created at 2020-12-07 19:08:03
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuServer {
  /**
   * 日志
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * 属性
   */
  @NotNull
  private final JsfWebFluxProperties.ModbusDtuServer properties;

  /**
   * 会话管理
   */
  @NotNull
  private final ModbusDtuSessionManager sessionManager;

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
  public ModbusDtuServer(@NotNull JsfWebFluxProperties.ModbusDtuServer properties,
                         @NotNull ModbusDtuSessionManager sessionManager) {
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
        protected void initChannel(SocketChannel ch) {
          if (log.isTraceEnabled()) {
            log.trace("Channel({}) init", ch);
          }

          ch
            .pipeline()
            .addLast("encoder", new ModbusDtuEncoder())
            .addLast("decoder", new ModbusDtuDecoder())
            .addLast("handler", new Handler());
        }
      });

    // 绑定
    bindChannelFuture = bootstrap.bind(properties.getPort()).sync();

    if (bindChannelFuture.isSuccess()) {
      // log
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

  //--------------------------------------------------------------------------------------------------------------
  // ChannelInboundHandlerAdapter
  //--------------------------------------------------------------------------------------------------------------
  //region

  /**
   *
   */
  class Handler extends ChannelInboundHandlerAdapter {
    /**
     * {@inheritDoc}
     */
    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
      if (log.isTraceEnabled()) {
        log.trace("Channel({}) read: {}", ctx.channel(), msg.toString());
      }

      super.channelRead(ctx, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelReadComplete(@NotNull ChannelHandlerContext ctx) throws Exception {
      if (log.isTraceEnabled()) {
        log.trace("Channel({}) read complete", ctx.channel());
      }

      super.channelReadComplete(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(@NotNull ChannelHandlerContext ctx, @NotNull Throwable cause) throws Exception {
      if (log.isTraceEnabled()) {
        log.trace("Channel({}) exception caught", ctx.channel(), cause);
      }

      ctx.channel().close();

      super.exceptionCaught(ctx, cause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
      Channel channel = ctx.channel();

      // log
      if (log.isTraceEnabled()) {
        log.trace("Channel({}) active", channel);
      }

      // 创建会话
      ModbusDtuSession session = new ModbusDtuSession(sessionManager, channel);

      // 绑定会话
      channel.attr(AttributeKey.valueOf("session")).set(session);

      // 通知管理器
      sessionManager.onConnect(session);

      // super
      super.channelActive(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
      if (log.isTraceEnabled()) {
        log.trace("Channel({}) inactive", ctx.channel());
      }

      // 获取会话
      ModbusDtuSession session = (ModbusDtuSession) ctx.channel().attr(AttributeKey.valueOf("session")).get();

      // 关闭
      session.close();

      // super
      super.channelInactive(ctx);
    }
  }

  //endregion
}
