package com.free.will.dawn.server.handler;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicLong;

public class MetricsHandler extends ChannelDuplexHandler {

  private MeterRegistry meterRegistry = new LoggingMeterRegistry();

  private AtomicLong totalConnectionNum = new AtomicLong();

  public MetricsHandler() {
    meterRegistry.gauge("totalConnectionNum", totalConnectionNum);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    totalConnectionNum.incrementAndGet();
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    totalConnectionNum.decrementAndGet();
    super.channelInactive(ctx);
  }
}
