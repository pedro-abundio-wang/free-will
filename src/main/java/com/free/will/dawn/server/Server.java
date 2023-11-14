package com.free.will.dawn.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

import java.net.StandardSocketOptions;

public class Server {

  private static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));

  public static void main(String[] args) throws Exception {

    // acceptor thread pool for ServerSocketChannel
    EventLoopGroup bossGroup =
        new NioEventLoopGroup(
            NettyRuntime.availableProcessors() * 2, new DefaultThreadFactory("boss"));

    // io event thread pool for SocketChannel
    EventLoopGroup workerGroup =
        new NioEventLoopGroup(
            NettyRuntime.availableProcessors() * 2, new DefaultThreadFactory("worker"));

    // business pool
    EventExecutorGroup businessGroup =
        new UnorderedThreadPoolEventExecutor(
            NettyRuntime.availableProcessors() * 2, new DefaultThreadFactory("business"));

    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .option(NioChannelOption.SO_BACKLOG, 1024)
          .childOption(NioChannelOption.TCP_NODELAY, true)
          .childOption(NioChannelOption.SO_KEEPALIVE, true)
          .childHandler(new SocketChannelInitializer(businessGroup));

      serverBootstrap.bind(PORT).sync().channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
      businessGroup.shutdownGracefully();
    }
  }
}
