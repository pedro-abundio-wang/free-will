package com.free.will.dawn.server.codec;

import com.free.will.dawn.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ProtocolEncoder extends MessageToMessageEncoder<ResponseMessage> {
  @Override
  protected void encode(
      ChannelHandlerContext ctx, ResponseMessage responseMessage, List<Object> out)
      throws Exception {
    ByteBuf buffer = ctx.alloc().buffer();
    responseMessage.encode(buffer);
    out.add(buffer);
  }
}
