package com.free.will.dawn.server.codec;

import com.free.will.dawn.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out)
      throws Exception {
    RequestMessage requestMessage = new RequestMessage();
    requestMessage.decode(byteBuf);
    out.add(requestMessage);
  }
}
