package com.free.will.dawn.client.codec;

import com.free.will.dawn.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ProtocolEncoder extends MessageToMessageEncoder<RequestMessage> {
  @Override
  protected void encode(ChannelHandlerContext ctx, RequestMessage requestMessage, List<Object> out)
      throws Exception {
    ByteBuf buffer = ctx.alloc().buffer();
    requestMessage.encode(buffer);
    out.add(buffer);
  }
}
