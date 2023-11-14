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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.TimeUnit;

public class SocketChannelInitializer extends ChannelInitializer<NioSocketChannel> {

  private final EventExecutorGroup eventExecutorGroup;

  public SocketChannelInitializer(EventExecutorGroup eventExecutorGroup) {
    this.eventExecutorGroup = eventExecutorGroup;
  }

  @Override
  protected void initChannel(NioSocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
    pipeline.addLast("IdleDetector", new IdleStateHandler(0, 20, 0, TimeUnit.SECONDS));
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
