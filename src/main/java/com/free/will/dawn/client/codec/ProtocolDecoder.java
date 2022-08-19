package com.free.will.dawn.client.codec;

import com.free.will.dawn.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out)
      throws Exception {
    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.decode(byteBuf);
    out.add(responseMessage);
  }
}
