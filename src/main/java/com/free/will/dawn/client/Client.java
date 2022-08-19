package com.free.will.dawn.client;

import com.free.will.common.operation.OperationResult;
import com.free.will.dawn.client.handler.RequestMessagePendingCenter;
import com.free.will.dawn.message.RequestMessage;
import com.free.will.dawn.operation.auth.AuthOperation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

  private static final Logger logger = LoggerFactory.getLogger(Client.class);

  private static final String IP = System.getProperty("ip", "127.0.0.1");
  private static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));

  public static void main(String[] args) throws Exception {
    NioEventLoopGroup group = new NioEventLoopGroup(new DefaultThreadFactory("worker"));
    try {

      RequestMessagePendingCenter requestMessagePendingCenter = new RequestMessagePendingCenter();

      Bootstrap bootstrap = new Bootstrap();
      bootstrap
          .group(group)
          .channel(NioSocketChannel.class)
          .option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000)
          .handler(new SocketChannelInitializer(requestMessagePendingCenter));
      ChannelFuture channelFuture = bootstrap.connect(IP, PORT).sync();

      RequestMessage requestMessage =
          requestMessagePendingCenter.makeRequestMessage(new AuthOperation("admin"));
      channelFuture.channel().writeAndFlush(requestMessage);
      OperationResult operationResult =
          requestMessagePendingCenter.get(requestMessage.getMessageHeader().getMessageId());

      logger.info("operationResult = {}", operationResult);

      channelFuture.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }
}
