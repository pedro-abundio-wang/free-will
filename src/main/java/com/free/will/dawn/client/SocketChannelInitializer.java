package com.free.will.dawn.client;

import com.free.will.dawn.client.codec.*;
import com.free.will.dawn.client.handler.RequestMessagePendingCenter;
import com.free.will.dawn.client.handler.ResponseMessageDispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SocketChannelInitializer extends ChannelInitializer<NioSocketChannel> {

  private RequestMessagePendingCenter requestMessagePendingCenter;

  public SocketChannelInitializer(RequestMessagePendingCenter requestMessagePendingCenter) {
    this.requestMessagePendingCenter = requestMessagePendingCenter;
  }

  @Override
  protected void initChannel(NioSocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
    pipeline.addLast("FrameDecoder", new FrameDecoder());
    pipeline.addLast("FrameEncoder", new FrameEncoder());
    pipeline.addLast("ProtocolEncoder", new ProtocolEncoder());
    pipeline.addLast("ProtocolDecoder", new ProtocolDecoder());
    pipeline.addLast(
        "ResponseMessageDispatcher", new ResponseMessageDispatcher(requestMessagePendingCenter));
    pipeline.addLast("OperationToRequestMessageEncoder", new OperationToRequestMessageEncoder());
    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
  }
}
