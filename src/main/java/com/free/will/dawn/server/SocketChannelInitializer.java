package com.free.will.dawn.server;

import com.free.will.dawn.server.codec.FrameDecoder;
import com.free.will.dawn.server.codec.FrameEncoder;
import com.free.will.dawn.server.codec.ProtocolDecoder;
import com.free.will.dawn.server.codec.ProtocolEncoder;
import com.free.will.dawn.server.handler.MetricsHandler;
import com.free.will.dawn.server.handler.RequestMessageHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;

public class SocketChannelInitializer extends ChannelInitializer<NioSocketChannel> {

  private EventExecutorGroup eventExecutorGroup;

  public SocketChannelInitializer(EventExecutorGroup eventExecutorGroup) {
    this.eventExecutorGroup = eventExecutorGroup;
  }

  @Override
  protected void initChannel(NioSocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
    pipeline.addLast("FrameDecoder", new FrameDecoder());
    pipeline.addLast("FrameEncoder", new FrameEncoder());
    pipeline.addLast("ProtocolEncoder", new ProtocolEncoder());
    pipeline.addLast("ProtocolDecoder", new ProtocolDecoder());
    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
    pipeline.addLast("MetricsHandler", new MetricsHandler());
    pipeline.addLast("FlushConsolidationHandler", new FlushConsolidationHandler(256, true));
    pipeline.addLast(eventExecutorGroup, "RequestMessageHandler", new RequestMessageHandler());
  }
}
